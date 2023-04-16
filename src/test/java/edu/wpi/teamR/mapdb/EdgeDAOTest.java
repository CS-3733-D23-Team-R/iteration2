package edu.wpi.teamR.mapdb;
import edu.wpi.teamR.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EdgeDAOTest {
    private static Connection connection;
    private static NodeDAO nodeDAO;
    private static EdgeDAO edgeDAO;
    @BeforeAll
    static void startup() throws SQLException, ClassNotFoundException {
        Configuration.changeSchemaName("iteration2test");
        connection = Configuration.getConnection();
        nodeDAO = new NodeDAO(connection);
        edgeDAO = new EdgeDAO(connection);
    }
    @AfterAll
    static void end() throws SQLException {
        edgeDAO.deleteAllEdges();
        nodeDAO.deleteAllNodes();
    }
    @BeforeEach
    void reset() throws SQLException {
        edgeDAO.deleteAllEdges();
        nodeDAO.deleteAllNodes();
    }
    @Test
    void getEdgeByNodeTest() throws SQLException {
        Node leftNode = nodeDAO.addNode(10, 10, "Floor1", "Building1");
        Node rightNode = nodeDAO.addNode(20, 20, "Floor2", "Building2");
        Edge aEdge = edgeDAO.addEdge(leftNode.getNodeID(), rightNode.getNodeID());
        ArrayList<Edge> dummy = new ArrayList<Edge>();
        dummy.add(aEdge);
        ArrayList<Edge> output = edgeDAO.getEdgesByNode(leftNode.getNodeID());
        assertEquals(output.get(0).getStartNode(), dummy.get(0).getStartNode());
        assertEquals(output.get(0).getEndNode(), dummy.get(0).getEndNode());
    }
    @Test
    void deleteEdgeTest() throws SQLException {
        Node leftNode = nodeDAO.addNode(10, 10, "Floor1", "Building1");
        Node rightNode = nodeDAO.addNode(20, 20, "Floor2", "Building2");
        edgeDAO.addEdge(leftNode.getNodeID(), rightNode.getNodeID());
        edgeDAO.deleteEdge(leftNode.getNodeID(), rightNode.getNodeID());
        ArrayList<Edge> output = edgeDAO.getEdges();
        assertEquals(output.size(), 0);
    }
    @Test
    void deleteEdgesByNodeTest() throws SQLException {
        Node leftNode = nodeDAO.addNode(10, 10, "Floor1", "Building1");
        Node rightNode = nodeDAO.addNode(20, 20, "Floor2", "Building2");
        Edge aEdge = edgeDAO.addEdge(leftNode.getNodeID(), rightNode.getNodeID());
        Node secondRightNode = nodeDAO.addNode(30, 30, "Floor3", "Building3");
        Edge secondEdge = edgeDAO.addEdge(leftNode.getNodeID(), secondRightNode.getNodeID());
        ArrayList<Edge> output = edgeDAO.getEdges();
        assertEquals(output.size(), 2);
    }
    @Test
    void addEdgeTest() throws SQLException {
        Node leftNode = nodeDAO.addNode(10, 10, "Floor1", "Building1");
        Node rightNode = nodeDAO.addNode(20, 20, "Floor2", "Building2");
        Edge aEdge = edgeDAO.addEdge(leftNode.getNodeID(), rightNode.getNodeID());
        ArrayList<Edge> output = edgeDAO.getEdges();
        assertEquals(output.size(), 1);
    }
    @Test
    void deleteAllEdgesTest() throws SQLException {
        Node leftNode = nodeDAO.addNode(10, 10, "Floor1", "Building1");
        Node rightNode = nodeDAO.addNode(20, 20, "Floor2", "Building2");
        Edge aEdge = edgeDAO.addEdge(leftNode.getNodeID(), rightNode.getNodeID());
        edgeDAO.deleteAllEdges();
        ArrayList<Edge> output = edgeDAO.getEdges();
        assertEquals(output.size(), 0);
    }
}
