package edu.wpi.teamR.mapdb;

import edu.wpi.teamR.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class MoveDAOTest {

    static private Connection connection;
    static private LocationNameDAO locationNameDAO;
    static private NodeDAO nodeDAO;
    static private MoveDAO moveDAO;

    @BeforeAll
    static void starterFunction() throws SQLException, ClassNotFoundException {
        Configuration.changeSchemaName("iteration2test");
        connection = Configuration.getConnection();
        locationNameDAO = new LocationNameDAO(connection);
        nodeDAO = new NodeDAO(connection);
        moveDAO = new MoveDAO(connection);
    }

    @BeforeEach
    void deleteOldData() throws SQLException {
        moveDAO.deleteAllMoves();
        nodeDAO.deleteAllNodes();
        locationNameDAO.deleteAllLocationNames();
    }

    @AfterAll
    static void clearDataDeleteConnection() throws SQLException {
        moveDAO.deleteAllMoves();
        nodeDAO.deleteAllNodes();
        locationNameDAO.deleteAllLocationNames();
    }


    @Test
    void getMoves() throws SQLException {
        ArrayList<Move> moves;
        Move move;
        Node node;
        Date date;

        moves = moveDAO.getMoves();
        assertEquals(moves.size(), 0);

        node = nodeDAO.addNode(0, 0, "L1", "temp");
        locationNameDAO.addLocationName("name1", "", "type");
        date = Date.valueOf(LocalDate.now());
        move = moveDAO.addMove(node.getNodeID(), "name1", date);

        moves = moveDAO.getMoves();
        assertEquals(moves.size(), 1);
        move = moves.get(0);
        assertEquals(move.getNodeID(), node.getNodeID());
        assertEquals(move.getLongName(), "name1");
    }

    @Test
    void getMovesByNodeID() throws SQLException {
        ArrayList<Move> moves;
        Node node;
        Date date;

        node = nodeDAO.addNode(0, 0, "L1", "temp");
        locationNameDAO.addLocationName("name1", "", "type");
        date = Date.valueOf(LocalDate.now().minusDays(0));
        moveDAO.addMove(node.getNodeID(), "name1", date);

        moves = moveDAO.getMovesByNodeID(node.getNodeID()); //after first move added
        assertEquals(moves.size(), 1);

        date = Date.valueOf(LocalDate.now().minusDays(1));
        moveDAO.addMove(node.getNodeID(), "name1", date);

        moves = moveDAO.getMovesByNodeID(node.getNodeID()); //after 2nd move added
        assertEquals(moves.size(), 2);

        moves = moveDAO.getMovesByNodeID(node.getNodeID()+1); //invalid id //TODO: need to replace with error case
        assertEquals(moves.size(), 0);
    }

    /*
    Think method isn't helpful
    @Test
    void getLatestMoveByNodeID() {

    }
     */

    /*
    Tested alongside others
    @Test
    void addMove() {
    }
     */

    @Test
    void deleteMovesByNode() throws SQLException {
        ArrayList<Move> moves;
        Node node;
        Node node2;
        Date date;

        node = nodeDAO.addNode(0, 0, "L1", "temp");
        node2 = nodeDAO.addNode(0, 0, "L1", "temp");
        locationNameDAO.addLocationName("name1", "", "type");
        date = Date.valueOf(LocalDate.now().minusDays(0));
        moveDAO.addMove(node.getNodeID(), "name1", date);

        moves = moveDAO.getMovesByNodeID(node.getNodeID()); //after first move added
        assertEquals(moves.size(), 1);

        moveDAO.deleteMovesByNode(node.getNodeID());

        moves = moveDAO.getMovesByNodeID(node.getNodeID()); //after first move deleted
        assertEquals(moves.size(), 0);


        date = Date.valueOf(LocalDate.now().minusDays(0));
        moveDAO.addMove(node.getNodeID(), "name1", date);
        date = Date.valueOf(LocalDate.now().minusDays(1));
        moveDAO.addMove(node.getNodeID(), "name1", date);
        date = Date.valueOf(LocalDate.now().minusDays(2));
        moveDAO.addMove(node2.getNodeID(), "name1", date);

        moves = moveDAO.getMovesByNodeID(node.getNodeID()); //after three moves added
        assertEquals(moves.size(), 2);

        moveDAO.deleteMovesByNode(node.getNodeID());

        moves = moveDAO.getMovesByNodeID(node2.getNodeID()); //after two moves deleted
        assertEquals(moves.size(), 1);

        moveDAO.deleteMovesByNode(node2.getNodeID());

        moves = moveDAO.getMovesByNodeID(node.getNodeID()); //after last move deleted
        assertEquals(moves.size(), 0);
    }

    @Test
    void deleteMovesByLongName() throws SQLException {
        ArrayList<Move> moves;
        Node node;
        Date date;

        node = nodeDAO.addNode(0, 0, "L1", "temp");
        locationNameDAO.addLocationName("name1", "", "type");
        locationNameDAO.addLocationName("name2", "", "type");

        moves = moveDAO.getMovesByNodeID(node.getNodeID()); //before anything added
        assertEquals(moves.size(), 0);

        date = Date.valueOf(LocalDate.now().minusDays(0));
        moveDAO.addMove(node.getNodeID(), "name1", date);
        date = Date.valueOf(LocalDate.now().minusDays(1));
        moveDAO.addMove(node.getNodeID(), "name2", date);

        moves = moveDAO.getMovesByNodeID(node.getNodeID()); //after both added
        assertEquals(moves.size(), 2);

        moveDAO.deleteMove(node.getNodeID(), "name2", date);

        moves = moveDAO.getMovesByNodeID(node.getNodeID()); //after one deleted
        assertEquals(moves.size(), 1);

        moveDAO.deleteMovesByLongName("fake");

        moves = moveDAO.getMovesByNodeID(node.getNodeID()); //after fake delete
        assertEquals(moves.size(), 1);

        moveDAO.deleteMovesByLongName("name1");

        moves = moveDAO.getMovesByNodeID(node.getNodeID()); //after last one deleted
        assertEquals(moves.size(), 0);
    }
}