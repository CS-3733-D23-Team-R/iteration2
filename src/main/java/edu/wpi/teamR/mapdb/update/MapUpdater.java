package edu.wpi.teamR.mapdb.update;

import edu.wpi.teamR.ItemNotFoundException;
import edu.wpi.teamR.mapdb.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.SQLException;
import java.util.*;

public class MapUpdater {
    ArrayDeque<UndoAction> actionQueue;
    UndoAction currentAction;
    private final MapDatabase mapdb;

    private int cur_temp_id;

    List<Node> nodes;
    List<Edge> edges;
    List<LocationName> locationNames;
    List<Move> moves;

    List<List<Node>> floors;
    Map<Node, List<Node>> edgeMap;
    Map<Integer, Node> nodeMap;

    String[] nodeFloorNames = {
            "L2",
            "L1",
            "1",
            "2",
            "3"
    };


    public MapUpdater(MapDatabase mapDatabase) throws SQLException {
        actionQueue = new ArrayDeque<>();
        mapdb = mapDatabase;
        cur_temp_id = -1;

        edges = mapdb.getEdges();
        locationNames = mapdb.getLocationNames();
        moves = mapdb.getMoves();
        floors = new ArrayList<>(5);
        nodeMap = new HashMap<>();
        edgeMap = new HashMap<>();

        for (Node n : nodes) {
            nodeMap.put(n.getNodeID(), n);
        }

        for (int i = 0; i < 5; i++) {
            nodes = mapdb.getNodesByFloor(nodeFloorNames[i]);
            List<Node> floorNodes = new ArrayList<>();
            floors.add(floorNodes);
            for (Node n : nodes) {
                nodeMap.put(n.getNodeID(), n);
            }
        }

        for (Edge e : edges) {
            edgeMap.put(nodeMap.get(e.getStartNode()), e.getEndNode());
        }
    }

    public void startAction() {
        if (currentAction != null)
            actionQueue.addFirst(currentAction);
        currentAction = new UndoAction();
    }

    public void endAction() {
        if (currentAction != null)
            actionQueue.addFirst(currentAction);
        currentAction = null;
    }

    public void submitUpdates() throws SQLException {
        if (currentAction != null) actionQueue.addFirst(currentAction);
        List<UpdateData> action;
        List<Integer> nodeIDs = new ArrayList<>();
        try {
            while (!actionQueue.isEmpty()) {
                action = actionQueue.removeLast().getUndos();
                for (UpdateData data : action) {
                    if (data.method().getName().equals("addNode")) {
                        Node returnedNode = (Node)data.method().invoke(mapdb, data.args());
                        nodeIDs.add(returnedNode.getNodeID());
                    } else if (data.method().getName().equals("addEdge")) {
                        int start = (int)data.args()[0];
                        int startNode = (start < 0) ? nodeIDs.get(Math.abs(start) - 1) : start;
                        int end = (int)data.args()[1];
                        int endNode = (end < 0) ? nodeIDs.get(Math.abs(end) - 1) : end;
                        data.method().invoke(mapdb, (Object)new Object[]{startNode, endNode});
                        continue;
                    }
                    data.method().invoke(mapdb, data.args());
                }
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public List<UndoData> undo() {
        if (currentAction != null) actionQueue.addFirst(currentAction);
        return actionQueue.removeFirst().getUndos();
    }


    public Node addNode(int xCoord, int yCoord, String floorNum, String building) {
        if (currentAction == null) currentAction = new UndoAction();
        Node n = new Node(cur_temp_id, xCoord, yCoord, floorNum, building);
        cur_temp_id--;
        floors.get(getFloorNum(floorNum)).add(n);
        nodeMap.put(cur_temp_id, n);
        currentAction.addUpdate(n, EditType.ADDITION);
        return n;
    }

    public Node modifyCoords(int nodeID, int xCoord, int yCoord) throws SQLException, ItemNotFoundException {
        if (currentAction == null) currentAction = new UndoAction();
        Node oldNode = nodeMap.get(nodeID);
        Node newNode = new Node(oldNode.getNodeID(), xCoord, yCoord, oldNode.getFloorNum(), oldNode.getBuilding());
        nodeMap.replace(nodeID, newNode);
        currentAction.addUpdate(oldNode, EditType.MODIFICATION);
        return newNode;
    }

    public void deleteNode(int nodeID) throws SQLException {
        if (currentAction == null) currentAction = new UndoAction();
        deleteEdgesByNode(nodeID);
        deleteMovesByNode(nodeID);
        Node n = nodeMap.remove(nodeID);
        floors.get(getFloorNum(n.getFloorNum())).remove(n);
        currentAction.addUpdate(n, EditType.DELETION);
    }


    public Edge addEdge(int startNode, int endNode) {
        if (currentAction == null) currentAction = new UndoAction();
        Edge edge = new Edge(startNode, endNode);
        currentAction.addUpdate(edge, EditType.ADDITION);
        edges.add(edge);
        return edge;
    }

    public void deleteEdge(int startNode, int endNode) {
        if (currentAction == null) currentAction = new UpdateAction();
        Method m;
        Edge edge = new Edge(startNode, endNode);
        try {
            m = mapdb.getClass().getMethod("deleteEdge", int.class, int.class);
            currentAction.addUpdate(m, new Object[]{startNode, endNode}, edge, EditType.DELETION);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteEdgesByNode(int nodeID) throws SQLException {
        if (currentAction == null) currentAction = new UpdateAction();
        Method m;
        List<Edge> edges = mapdb.getEdgesByNode(nodeID);
        try {
            m = mapdb.getClass().getMethod("deleteEdgesByNode", int.class);
            currentAction.addUpdate(m, new Object[]{nodeID}, edges);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }


    public Move addMove(int nodeID, String longName, Date moveDate) {
        if (currentAction == null) currentAction = new UpdateAction();
        Method m;
        Move move = new Move(nodeID, longName, moveDate);
        try {
            m = mapdb.getClass().getMethod("addMove", int.class, String.class, Date.class);
            currentAction.addUpdate(m, new Object[]{nodeID, longName, moveDate}, move, EditType.ADDITION);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return move;
    }

    public void deleteMovesByNode(int nodeID) throws SQLException {
        if (currentAction == null) currentAction = new UpdateAction();
        Method m;
        ArrayList<Move> moves = mapdb.getMovesByNode(nodeID);
        try {
            m = mapdb.getClass().getMethod("deleteMovesByNode", int.class);
            currentAction.addUpdate(m, new Object[]{nodeID}, moves);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteMovesByLocationName(String longName) throws SQLException, ItemNotFoundException {
        if (currentAction == null) currentAction = new UpdateAction();
        Method m;
        Move move = mapdb.getLatestMoveByLocationName(longName);
        try {
            m = mapdb.getClass().getMethod("deleteMovesByLocationName", String.class);
            currentAction.addUpdate(m, new Object[]{longName}, move, EditType.DELETION);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }


    public LocationName modifyLocationNameType(String longName, String newType) throws SQLException, ItemNotFoundException {
        if (currentAction == null) currentAction = new UpdateAction();
        Method m;
        LocationName oldl = mapdb.getLocationNameByLongName(longName);
        LocationName newl = new LocationName(longName, oldl.getShortName(), newType);
        try {
            m = mapdb.getClass().getMethod("modifyLocationNameType", String.class, String.class);
            currentAction.addUpdate(m, new Object[]{longName, newType}, oldl, EditType.MODIFICATION);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return newl;
    }

    public LocationName modifyLocationNameShortName(String longName, String newShortName) throws SQLException, ItemNotFoundException {
        if (currentAction == null) currentAction = new UpdateAction();
        Method m;
        LocationName oldl = mapdb.getLocationNameByLongName(longName);
        LocationName newl = new LocationName(oldl.getLongName(), newShortName, oldl.getNodeType());
        try {
            m = mapdb.getClass().getMethod("modifyLocationNameShortName", String.class, String.class);
            currentAction.addUpdate(m, new Object[]{longName, newShortName}, oldl, EditType.MODIFICATION);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return newl;
    }

    public LocationName addLocationName(String longName, String shortName, String nodeType) {
        if (currentAction == null) currentAction = new UpdateAction();
        Method m;
        LocationName l = new LocationName(longName, shortName, nodeType);
        try {
            m = mapdb.getClass().getMethod("addLocationName", String.class, String.class, String.class);
            currentAction.addUpdate(m, new Object[]{longName, shortName, nodeType}, l, EditType.ADDITION);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return l;
    }

    public void deleteLocationName(String longName) throws SQLException, ItemNotFoundException {
        if (currentAction == null) currentAction = new UpdateAction();
        Method m;
        LocationName l = mapdb.getLocationNameByLongName(longName);
        deleteMovesByLocationName(longName);
        try {
            m = mapdb.getClass().getMethod("deleteLocationName", String.class);
            currentAction.addUpdate(m, new Object[]{longName}, l, EditType.DELETION);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private int getFloorNum(String floor) {
        for (int i = 0; i < nodeFloorNames.length; i++) {
            if (nodeFloorNames[i].equals(floor))
                return i;
        }
        throw new RuntimeException("Floor not found");
    }
}
