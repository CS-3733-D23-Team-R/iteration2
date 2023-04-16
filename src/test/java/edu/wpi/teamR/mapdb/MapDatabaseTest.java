package edu.wpi.teamR.mapdb;

import edu.wpi.teamR.Configuration;
import edu.wpi.teamR.ItemNotFoundException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;

import static edu.wpi.teamR.mapdb.NodeDAO.parseNodes;
import static org.junit.jupiter.api.Assertions.*;

class MapDatabaseTest {

    static private Connection connection;
    static private MapDatabase mapDatabase;
    static private NodeDAO nodeDAO;
    static private EdgeDAO edgeDAO;
    static private MoveDAO moveDAO;
    static private LocationNameDAO locationNameDAO;
    @BeforeAll
    static void starterFunction() throws SQLException, ClassNotFoundException {
        Configuration.changeSchemaName("iteration2test");
        connection = Configuration.getConnection();
        nodeDAO = new NodeDAO(connection);
        edgeDAO = new EdgeDAO(connection);
        moveDAO = new MoveDAO(connection);
        locationNameDAO = new LocationNameDAO(connection);
        mapDatabase = new MapDatabase();
    }

    @BeforeEach
    void deleteOldData() throws SQLException {
        edgeDAO.deleteAllEdges();
        moveDAO.deleteAllMoves();
        nodeDAO.deleteAllNodes();
        locationNameDAO.deleteAllLocationNames();
    }

    @AfterAll
    static void clearDataDeleteConnection() throws SQLException {
        edgeDAO.deleteAllEdges();
        moveDAO.deleteAllMoves();
        nodeDAO.deleteAllNodes();
        locationNameDAO.deleteAllLocationNames();
    }

    @Test
    public void getNodesByTypeTest() throws SQLException {
        Node node1 = mapDatabase.addNode(1, 0, "1", "temp");
        Node node2 = mapDatabase.addNode(2, 0, "1", "temp");
        Node node3 = mapDatabase.addNode(3, 0, "1", "temp");
        Node node4 = mapDatabase.addNode(4, 0, "1", "temp");
        Node node5 = mapDatabase.addNode(5, 0, "1", "temp");
        Node node6 = mapDatabase.addNode(6, 0, "1", "temp");

        LocationName locationName1 = mapDatabase.addLocationName("location1", "", "typ1");
        LocationName locationName2 = mapDatabase.addLocationName("location2", "", "typ2");
        LocationName locationName3 = mapDatabase.addLocationName("location3", "", "typ3");
        LocationName locationName4 = mapDatabase.addLocationName("location4", "", "typ4");
        LocationName locationName5 = mapDatabase.addLocationName("location5", "", "typ4");

        Move move1 = mapDatabase.addMove(node1.getNodeID(), locationName1.getLongName(), new Date(System.currentTimeMillis())); //should remain
        Move move2 = mapDatabase.addMove(node2.getNodeID(), locationName2.getLongName(), new Date(System.currentTimeMillis() - 1000000000));
        Move move3 = mapDatabase.addMove(node1.getNodeID(), locationName2.getLongName(), new Date(System.currentTimeMillis())); //should remain
        Move move4 = mapDatabase.addMove(node3.getNodeID(), locationName3.getLongName(), new Date(System.currentTimeMillis())); //should remain
        Move move5 = mapDatabase.addMove(node3.getNodeID(), locationName4.getLongName(), new Date(System.currentTimeMillis())); //should remain
        Move move6 = mapDatabase.addMove(node4.getNodeID(), locationName5.getLongName(), new Date(System.currentTimeMillis() - 1000000000));
        Move move7 = mapDatabase.addMove(node4.getNodeID(), locationName5.getLongName(), new Date(System.currentTimeMillis())); //should remain
        ArrayList<Node> nodes;
        int count = 0;
        nodes = mapDatabase.getNodesByType("typ1");
        count += nodes.size();
        assertEquals(nodes.size(), 1);
        nodes = mapDatabase.getNodesByType("typ2");
        count += nodes.size();
        assertEquals(nodes.size(), 1);
        nodes = mapDatabase.getNodesByType("typ3");
        count += nodes.size();
        assertEquals(nodes.size(), 1);
        nodes = mapDatabase.getNodesByType("typ4");
        count += nodes.size();
        assertEquals(nodes.size(), 2);
        nodes = mapDatabase.getNodesByType("typ5");
        count += nodes.size();
        assertEquals(nodes.size(), 0);
        assertEquals(count, 5);
    }
    @Test
    public void getEdgesByFloorTest() {
        assertTrue(true);
    }
    @Test
    public void getLatestMoveByLocationNameTest() {
        assertTrue(true);
    }
    @Test
    public void getNodeTypeByNodeIDTest() {
        assertTrue(true);
    }
    @Test
    public void getMapLocationsByFloorTest() {
        assertTrue(true);
    }

}