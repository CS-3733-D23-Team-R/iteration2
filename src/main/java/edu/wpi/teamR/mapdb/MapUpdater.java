package edu.wpi.teamR.mapdb;

import edu.wpi.teamR.ItemNotFoundException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class MapUpdater {
    ArrayDeque<UpdateAction> actionQueue;
    UpdateAction currentAction;
    MapDatabase mapdb;

    private int cur_temp_id;


    public MapUpdater(MapDatabase mapDatabase) {
        actionQueue = new ArrayDeque<>();
        mapdb = mapDatabase;
        cur_temp_id = -1;
    }

    public void startAction() {
        if (currentAction != null)
            actionQueue.addFirst(currentAction);
        currentAction = new UpdateAction();
    }

    public void endAction() {
        if (currentAction != null)
            actionQueue.addFirst(currentAction);
        currentAction = null;
    }

    public void submitUpdates() throws SQLException {
        if (currentAction != null) actionQueue.addFirst(currentAction);
        List<Pair<Method, Object[]>> action;
        try {
            while (!actionQueue.isEmpty()) {
                action = actionQueue.removeLast().getUpdates();
                for (Pair<Method, Object[]> p : action) {
                    p.first.invoke(p.second);
                }
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Deque<UndoData> undo() {
        return actionQueue.removeFirst().getUndoData();
    }


    public Node addNode(int xCoord, int yCoord, String floorNum, String building) {
        if (currentAction == null) currentAction = new UpdateAction();
        Method m;
        Node n = new Node(cur_temp_id, xCoord, yCoord, floorNum, building);
        cur_temp_id--;
        try {
            m = mapdb.getClass().getMethod("addNode", int.class, int.class, String.class, String.class);
            currentAction.addUpdate(m, new Object[]{xCoord, yCoord, floorNum, building}, n, EditType.ADDITION);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return n;
    }

    public Node modifyCoords(int nodeID, int xCoord, int yCoord) throws SQLException {
        if (currentAction == null) currentAction = new UpdateAction();
        Method m;
        Node oldNode = mapdb.getNodeByID(nodeID);
        Node newNode = new Node(oldNode.getNodeID(), xCoord, yCoord, oldNode.getFloorNum(), oldNode.getBuilding());
        try {
            m = mapdb.getClass().getMethod("modifyCoords", int.class, int.class, int.class);
            currentAction.addUpdate(m, new Object[]{nodeID, xCoord, yCoord}, oldNode, EditType.MODIFICATION);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return newNode;
    }

    public void deleteNode(int nodeID) throws SQLException {
        if (currentAction == null) currentAction = new UpdateAction();
        deleteEdgesByNode(nodeID);
        deleteMovesByNode(nodeID);
        Method m;
        try {
            m = mapdb.getClass().getMethod("deleteNode", int.class);
            currentAction.addUpdate(m, new Object[]{nodeID}, mapdb.getNodeByID(nodeID), EditType.DELETION);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }


    public Edge addEdge(int startNode, int endNode) {
        if (currentAction == null) currentAction = new UpdateAction();
        Method m;
        Edge edge = new Edge(startNode, endNode);
        try {
            m = mapdb.getClass().getMethod("addEdge", int.class, int.class);
            currentAction.addUpdate(m, new Object[]{startNode, endNode}, edge, EditType.ADDITION);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
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
        LocationName l = mapdb.getLocationNameByLongName(longName);
        l.setNodeType(newType);
        try {
            m = mapdb.getClass().getMethod("modifyLocationNameType", String.class, String.class);
            currentAction.addUpdate(m, new Object[]{longName, newType}, l, EditType.MODIFICATION);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return l;
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
}
