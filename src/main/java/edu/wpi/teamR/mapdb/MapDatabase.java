package edu.wpi.teamR.mapdb;

import edu.wpi.teamR.Configuration;
import edu.wpi.teamR.ItemNotFoundException;
import edu.wpi.teamR.csv.CSVParameterException;
import edu.wpi.teamR.csv.CSVReader;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

import static edu.wpi.teamR.mapdb.NodeDAO.parseNodes;

public class MapDatabase {
    //    private MapDatabase instance;
    private Connection connection;
    private NodeDAO nodeDao;
    private EdgeDAO edgeDao;
    private MoveDAO moveDao;
    private LocationNameDAO locationNameDao;

    public MapDatabase() throws SQLException, ClassNotFoundException {
        this.connection = Configuration.getConnection();
        this.nodeDao = new NodeDAO(connection);
        this.edgeDao = new EdgeDAO(connection);
        this.moveDao = new MoveDAO(connection);
        this.locationNameDao = new LocationNameDAO(connection);
    }

    // old code for making mapDatabase singleton
//    public MapDatabase getInstance() {
//        if (instance != null)
//            instance = new MapDatabase();
//        return instance;
//    }

    public ArrayList<Node> getNodes() throws SQLException {
        return nodeDao.getNodes();
    }

    public Node getNodeByID(int nodeID) throws SQLException, ItemNotFoundException {
        return nodeDao.getNodeByID(nodeID);
    }

    public ArrayList<Node> getNodesByFloor(String floor) throws SQLException {
        return nodeDao.getNodesByFloor(floor);
    }

    public ArrayList<Node> getNodesByType(String type) throws SQLException { //TODO: GET CHECKED
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM "+Configuration.getNodeSchemaNameTableName()+" NATURAL JOIN (SELECT * FROM "+Configuration.getMoveSchemaNameTableName()+" NATURAL JOIN (SELECT longname, MAX(date) as date from "+Configuration.getMoveSchemaNameTableName()+" WHERE date<now() group by longname) as foo) as foo natural join "+Configuration.getLocationNameSchemaNameTableName()+" WHERE nodetype=? ORDER BY nodeID;");
        preparedStatement.setString(1, type);
        ResultSet resultSet = preparedStatement.executeQuery();
        return parseNodes(resultSet);
    }

    public Node addNode(int xCoord, int yCoord, String floorNum, String building) throws SQLException {
        return nodeDao.addNode(xCoord, yCoord, floorNum, building);
    }

    public Node modifyCoords(int nodeID, int newXCoord, int newYCoord) throws SQLException {
        return nodeDao.modifyCoords(nodeID, newXCoord, newYCoord);
    }

    public void deleteNode(int nodeID) throws SQLException {
        nodeDao.deleteNode(nodeID);
    }

    public ArrayList<Edge> getEdges() throws SQLException {
        return edgeDao.getEdges();
    }

    public ArrayList<Edge> getEdgesByNode(int nodeID) throws SQLException {
        return edgeDao.getEdgesByNode(nodeID);
    }

    public ArrayList<Edge> getEdgesByFloor(String floor) throws SQLException {
        ArrayList<Edge> temp = new ArrayList<>();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select startnode,endnode from "+Configuration.getNodeSchemaNameTableName()+" join "+Configuration.getEdgeSchemaNameTableName()+" on node.nodeid = edge.startnode or node.nodeid = edge.endnode where floor = '"+floor+"';");
        while(resultSet.next()){
            Edge aEdge = new Edge(resultSet.getInt("startnode"), resultSet.getInt("endnode"));
            temp.add(aEdge);
        }
        return temp;
    }

    public Edge addEdge(int startNodeID, int endNodeID) throws SQLException {
        return edgeDao.addEdge(startNodeID, endNodeID);
    }

    public void deleteEdge(int startNodeID, int endNodeID) throws SQLException {
        edgeDao.deleteEdge(startNodeID, endNodeID);
    }

    public void deleteEdgesByNode(int nodeID) throws SQLException {
        edgeDao.deleteEdgesByNode(nodeID);
    }

    public ArrayList<Integer> getAdjacentNodeIDsByNodeID(int nodeID) throws SQLException {
        return edgeDao.getAdjacentNodeIDsByNodeID(nodeID);
    }

    public ArrayList<Move> getMoves() throws SQLException {
        return moveDao.getMoves();
    }

    public ArrayList<Move> getMovesByNode(int nodeID) throws SQLException {
        return moveDao.getMovesByNodeID(nodeID);
    }

    public Move getLatestMoveByLocationName(String longName) throws SQLException, ItemNotFoundException { //TODO: GET CHECKED
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM "+Configuration.getMoveSchemaNameTableName()+" WHERE date=(select max(date) FROM "+Configuration.getMoveSchemaNameTableName()+" WHERE longname = ? AND date<now()) AND longname = ?;");
        preparedStatement.setString(1, longName);
        preparedStatement.setString(2, longName);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()){
            int nodeID = resultSet.getInt("nodeID");
            Date date = resultSet.getDate("date");

            return new Move(nodeID, longName, date);
        }
        throw new ItemNotFoundException();
    }

    public Move addMove(int nodeID, String longName, Date moveDate) throws SQLException {
        return moveDao.addMove(nodeID, longName, moveDate);
    }

    public void deleteMovesByNode(int nodeID) throws SQLException {
        moveDao.deleteMovesByNode(nodeID);
    }

    public void deleteMove(int nodeID, String longname, Date moveDate) throws SQLException {
        moveDao.deleteMove(nodeID, longname, moveDate);
    }

    public LocationName addLocationName(String longname, String shortname, String nodetype) throws SQLException {
        return locationNameDao.addLocationName(longname, shortname, nodetype);
    }

    public void deleteLocationName(String longname) throws SQLException {
        locationNameDao.deleteLocationName(longname);
    }

    public void deleteMovesByLocationName(String longName) throws SQLException {
        moveDao.deleteMovesByLongName(longName);
    }

    public ArrayList<LocationName> getLocationNames() throws SQLException {
        return locationNameDao.getLocations();
    }

    public ArrayList<LocationName> getLocationNamesByNodeType(String nodeType) throws SQLException {
        return locationNameDao.getLocationsByNodeType(nodeType);
    }

    public String getNodeTypeByNodeID(int nodeID) throws SQLException, ItemNotFoundException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT nodetype from (SELECT * FROM (SELECT longname, MAX(date) as date from "+Configuration.getMoveSchemaNameTableName()+" WHERE date<now() AND nodeid=? group by longname) as foo ORDER BY date desc limit 1) as foo NATURAL JOIN "+Configuration.getLocationNameSchemaNameTableName()+";");
        preparedStatement.setInt(1, nodeID);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next())
            return resultSet.getString("nodetype");

        throw new ItemNotFoundException();
    }


    public LocationName getLocationNameByLongName(String longName) throws SQLException, ItemNotFoundException {
        return locationNameDao.getLocationByLongName(longName);
    }


    public LocationName modifyLocationNameType(String longName, String newType) throws SQLException {
        return locationNameDao.modifyLocationNameType(longName, newType);
    }

    public LocationName modifyLocationNameShortName(String longName, String newShortName) throws SQLException {
        return locationNameDao.modifyLocationNameShortName(longName, newShortName);
    }

    public ArrayList<MapLocation> getMapLocationsByFloor(String floor) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM "+Configuration.getNodeSchemaNameTableName()+" node LEFT JOIN (SELECT * FROM "+Configuration.getMoveSchemaNameTableName()+" NATURAL JOIN (SELECT longname, MAX(date) as date from "+Configuration.getMoveSchemaNameTableName()+" WHERE date<now() group by longname) as foo) as move on node.nodeid=move.nodeid left join "+Configuration.getLocationNameSchemaNameTableName()+" locationname on move.longname=locationname.longname ORDER BY node.nodeID desc;");
        ResultSet resultSet = preparedStatement.executeQuery();

        ArrayList<MapLocation> mapLocations = new ArrayList<>();
        Node lastNode = new Node(-1, 0, 0, "", ""); //need to initialize so that it doesm't error
        Node currentNode;
        ArrayList<LocationName> locationNames = new ArrayList<>();
        LocationName locationName;
        while (resultSet.next()){
            int nodeID = resultSet.getInt("nodeid");
            int xCoord = resultSet.getInt("xCoord");
            int yCoord = resultSet.getInt("yCoord");
            String building = resultSet.getString("building");
            String longName = resultSet.getString("longName");
            String shortName = resultSet.getString("shortName");
            String nodeType = resultSet.getString("nodeType");

            currentNode = new Node(nodeID, xCoord, yCoord, floor, building);
            locationName = new LocationName(longName, shortName, nodeType);

            boolean noLocationNameForNode = longName==null && shortName==null && nodeType==null;
            boolean continuingLastNode = lastNode.getNodeID()==nodeID;
            if (continuingLastNode){
                if (!noLocationNameForNode) {
                    locationNames.add(locationName);
                }
            } else {
                mapLocations.add(new MapLocation(lastNode, locationNames)); //if you've reached the end of the list of locations then you're ready to add it
                locationNames = new ArrayList<>(); //reset list for the next node
                if (!noLocationNameForNode) {
                    locationNames.add(locationName); //add first entry for the next node
                }
            }

            lastNode = currentNode;
        }
        mapLocations.add(new MapLocation(lastNode, locationNames)); //the last locationName will already have been added

        return mapLocations;
    }

    public ArrayList<? extends MapData> readCSV(String path, Class<? extends MapData> _class) throws IOException, CSVParameterException, SQLException {
        String[] fullName = _class.getName().split("[.]");
        String name = fullName[fullName.length - 1];
        switch (name) {
            case "Node" -> {
                CSVReader<Node> reader = new CSVReader<>(path, Node.class);
                ArrayList<Node> nodes = reader.parseCSV();
                edgeDao.deleteAllEdges();
                moveDao.deleteAllMoves();
                nodeDao.deleteAllNodes();
                nodeDao.addNodes(nodes);
                return nodes;
            }
            case "Edge" -> {
                CSVReader<Edge> reader = new CSVReader<>(path, Edge.class);
                ArrayList<Edge> edges = reader.parseCSV();
                edgeDao.deleteAllEdges();
                for (Edge e : edges) {
                    edgeDao.addEdge(e.getStartNode(), e.getEndNode());
                }
                return edges;
            }
            case "Move" -> {
                CSVReader<Move> reader = new CSVReader<>(path, Move.class);
                ArrayList<Move> moves = reader.parseCSV();
                moveDao.deleteAllMoves();
                for (Move m : moves) {
                    moveDao.addMove(m.getNodeID(), m.getLongName(), m.getMoveDate());
                }
                return moves;
            }
            case "LocationName" -> {
                CSVReader<LocationName> reader = new CSVReader<>(path, LocationName.class);
                ArrayList<LocationName> locs = reader.parseCSV();
                moveDao.deleteAllMoves();
                locationNameDao.deleteAllLocationNames();
                for (LocationName l : locs) {
                    locationNameDao.addLocationName(l.getLongName(), l.getShortName(), l.getNodeType());
                }
                return locs;
            }
            default -> throw new IllegalStateException("Unexpected class name: " + _class.getName());
        }
        // return null;
    }
}
