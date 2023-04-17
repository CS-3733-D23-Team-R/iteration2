package edu.wpi.teamR.mapdb.update;

import edu.wpi.teamR.Configuration;
import edu.wpi.teamR.ItemNotFoundException;
import edu.wpi.teamR.mapdb.MapDatabase;
import edu.wpi.teamR.mapdb.Node;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MapUpdaterTest {
    static MapDatabase mapdb;

    @BeforeAll
    static void setUp() throws SQLException, ClassNotFoundException {
        Configuration.changeSchemaName("iteration2test");
        mapdb = new MapDatabase();
    }

    @BeforeEach
    void reset() throws SQLException, ClassNotFoundException {
        Connection c = Configuration.getConnection();
        c.createStatement().execute("DELETE FROM " + Configuration.getEdgeSchemaNameTableName());
        c.createStatement().execute("DELETE FROM " + Configuration.getMoveSchemaNameTableName());
        c.createStatement().execute("DELETE FROM " + Configuration.getLocationNameSchemaNameTableName());
        c.createStatement().execute("DELETE FROM " + Configuration.getNodeSchemaNameTableName());
    }

    @Test
    void addNodeUndo() {
        MapUpdater mapUpdater = new MapUpdater(mapdb);
        Node i = mapUpdater.addNode(0, 0, "e", "e2");
        List<UndoData> data = mapUpdater.undo();
        UndoData undo = data.get(0);
        Node f = (Node)undo.data();
        assertSame(i, f);
        assertEquals(EditType.ADDITION, undo.editType());
    }

    @Test
    void addNodeUpdate() throws SQLException {
        MapUpdater mapUpdater = new MapUpdater(mapdb);
        Node i = mapUpdater.addNode(32, 23, "floor", "building");
        mapUpdater.submitUpdates();
        Node f = mapdb.getNodes().get(0);
        assertEquals(32, f.getXCoord());
        assertEquals(23, f.getYCoord());
        assertEquals("floor", f.getFloorNum());
        assertEquals("building", f.getBuilding());
    }

    @Test
    void modifyCoordsUndo() throws SQLException, ItemNotFoundException {
        MapUpdater mapUpdater = new MapUpdater(mapdb);
        Node i = mapdb.addNode(12, 21, "flop", "bloud");
        Node u = mapUpdater.modifyCoords(i.getNodeID(), 32, 23);
        UndoData undo = mapUpdater.undo().get(0);
        Node f = (Node)undo.data();
        assertEquals(i.getNodeID(), u.getNodeID());
        assertEquals(i.getNodeID(), f.getNodeID());
        assertEquals(32, u.getXCoord());
        assertEquals(23, u.getYCoord());
        assertEquals("flop", u.getFloorNum());
        assertEquals("bloud", u.getBuilding());
        assertTrue(f.getNodeID() >= 0);
        assertEquals(12, f.getXCoord());
        assertEquals(21, f.getYCoord());
        assertEquals("flop", f.getFloorNum());
        assertEquals("bloud", f.getBuilding());
        assertEquals(EditType.MODIFICATION, undo.editType());
    }

    @Test
    void modifyCoordsUpdate() throws SQLException, ItemNotFoundException {
        MapUpdater mapUpdater = new MapUpdater(mapdb);
        Node i = mapdb.addNode(12, 21, "flop", "bloud");
        Node u = mapUpdater.modifyCoords(i.getNodeID(), 32, 23);
        mapUpdater.submitUpdates();
        Node f = mapdb.getNodes().get(0);
        assertEquals(i.getNodeID(), f.getNodeID());
        assertEquals(32, f.getXCoord());
        assertEquals(23, f.getYCoord());
        assertEquals("flop", f.getFloorNum());
        assertEquals("bloud", f.getBuilding());
    }

    @Test
    void deleteNodeUndo() throws SQLException {
        MapUpdater mapUpdater = new MapUpdater(mapdb);
        Node i = mapdb.addNode(12, 21, "flop", "bloud");
        mapUpdater.deleteNode(i.getNodeID());
        UndoData undo = mapUpdater.undo().get(0);
        Node f = (Node)undo.data();
        assertEquals(i.getNodeID(), f.getNodeID());
        assertEquals(12, f.getXCoord());
        assertEquals(21, f.getYCoord());
        assertEquals("flop", f.getFloorNum());
        assertEquals("bloud", f.getBuilding());
        assertEquals(EditType.DELETION, undo.editType());
    }

    @Test
    void deleteNodeUpdate() throws SQLException {
        MapUpdater mapUpdater = new MapUpdater(mapdb);
        Node i = mapdb.addNode(12, 21, "flop", "bloud");
        mapUpdater.deleteNode(i.getNodeID());
        UndoData undo = mapUpdater.undo().get(0);
        mapUpdater.submitUpdates();
        assertEquals(0, mapdb.getNodes().size());
        assertThrows(ItemNotFoundException.class, () -> {
            mapdb.getNodeByID(i.getNodeID());
        });
    }

    @Test
    void addEdge() {
    }

    @Test
    void deleteEdge() {
    }

    @Test
    void deleteEdgesByNode() {
    }

    @Test
    void addMove() {
    }

    @Test
    void deleteMovesByNode() {
    }

    @Test
    void deleteMovesByLocationName() {
    }

    @Test
    void modifyLocationNameType() {
    }

    @Test
    void modifyLocationNameShortName() {
    }

    @Test
    void addLocationName() {
    }

    @Test
    void deleteLocationName() {
    }
}