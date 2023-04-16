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

public class NodeDAOTest {
    private static Connection connection;

    private static NodeDAO nodeDAO;

    @BeforeAll
    static void startup() throws SQLException, ClassNotFoundException {
        Configuration.changeSchemaName("iteration2test");
        connection = Configuration.getConnection();
        nodeDAO = new NodeDAO(connection);
    }
    @AfterAll
    static void end() throws SQLException {
        nodeDAO.deleteAllNodes();
    }
    @BeforeEach
    void reset() throws SQLException {
        nodeDAO.deleteAllNodes();
    }
    @Test
    void getNodesTest() throws SQLException {
        ArrayList<Node> nodes;
        nodes = nodeDAO.getNodes();
        assertEquals(nodes.size(), 0);
        Node aNode =  nodeDAO.addNode(9, 10, "Floor1", "Building1");
        nodes = nodeDAO.getNodes();
        assertEquals(nodes.size(), 1);
    }
    @Test
    void getNodeByIDTest() throws SQLException {
        Node aNode = nodeDAO.addNode(11, 12, "Floor2", "Building2");
        assertEquals(aNode.getNodeID(), nodeDAO.getNodeByID(aNode.getNodeID()).getNodeID());
        assertEquals(aNode.getXCoord(), nodeDAO.getNodeByID(aNode.getNodeID()).getXCoord());
        assertEquals(aNode.getYCoord(), nodeDAO.getNodeByID(aNode.getNodeID()).getYCoord());
        assertEquals(aNode.getFloorNum(), nodeDAO.getNodeByID(aNode.getNodeID()).getFloorNum());
        assertEquals(aNode.getBuilding(), nodeDAO.getNodeByID(aNode.getNodeID()).getBuilding());
    }
    @Test
    void getNodesByFloorTest() throws SQLException {
        ArrayList<Node> nodes = new ArrayList<Node>();
        Node aNode = nodeDAO.addNode(13, 14, "Floor3", "Building3");
        Node aNode2 = nodeDAO.addNode(15, 16, "Floor3", "Building3");
        nodes.add(aNode);
        nodes.add(aNode2);
        for(Node node : nodes){
            assertEquals(node.getNodeID(), nodeDAO.getNodeByID(node.getNodeID()).getNodeID());
            assertEquals(node.getXCoord(), nodeDAO.getNodeByID(node.getNodeID()).getXCoord());
            assertEquals(node.getYCoord(), nodeDAO.getNodeByID(node.getNodeID()).getYCoord());
            assertEquals(node.getFloorNum(), nodeDAO.getNodeByID(node.getNodeID()).getFloorNum());
            assertEquals(node.getBuilding(), nodeDAO.getNodeByID(node.getNodeID()).getBuilding());
        }
    }
    @Test
    void addNodeTest() throws SQLException {
        Node node = nodeDAO.addNode(17, 18, "Floor4", "Building4");
        Node dummy = nodeDAO.getNodeByID(node.getNodeID());
        assertEquals(node.getNodeID(), dummy.getNodeID());
        assertEquals(node.getXCoord(), dummy.getXCoord());
        assertEquals(node.getYCoord(), dummy.getYCoord());
        assertEquals(node.getFloorNum(), dummy.getFloorNum());
        assertEquals(node.getBuilding(), dummy.getBuilding());
    }
    @Test
    void modifyCoordsTest() throws SQLException {
        Node aNode = nodeDAO.addNode(19,  20, "Floor5", "Building5");
        Node updatedNode = nodeDAO.modifyCoords(aNode.getNodeID(), 50, 50);
        Node dummy = nodeDAO.getNodeByID(aNode.getNodeID());
        assertEquals(updatedNode.getNodeID(), dummy.getNodeID());
        assertEquals(updatedNode.getXCoord(), dummy.getXCoord());
        assertEquals(updatedNode.getYCoord(), dummy.getYCoord());
        assertEquals(updatedNode.getFloorNum(), dummy.getFloorNum());
        assertEquals(updatedNode.getBuilding(), dummy.getBuilding());
    }
    @Test
    void deleteNodeTest() throws SQLException {
        Node aNode = nodeDAO.addNode(19, 20, "Floor6", "Building6");
        ArrayList<Node> nodes = new ArrayList<Node>();
        nodeDAO.deleteNode(aNode.getNodeID());
        nodes = nodeDAO.getNodes();
        assertEquals(nodes.size(), 0);
    }
    @Test
    void deleteAllNodesTest() throws SQLException {
        ArrayList<Node> nodes = new ArrayList<Node>();
        nodeDAO.addNode(21, 22, "Floor7", "Building7");
        nodeDAO.deleteAllNodes();
        nodes = nodeDAO.getNodes();
        assertEquals(nodes.size(), 0);
    }
}
