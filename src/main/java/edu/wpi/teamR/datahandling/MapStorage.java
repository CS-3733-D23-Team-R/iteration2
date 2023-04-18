package edu.wpi.teamR.datahandling;

import edu.wpi.teamR.Main;
import edu.wpi.teamR.mapdb.*;
import javafx.scene.image.ImageView;
import lombok.Getter;

import java.net.URL;
import java.sql.SQLException;
import java.util.*;

@Getter
public class MapStorage {

    static URL firstFloorLink = Main.class.getResource("images/01_thefirstfloor.png");
    static URL secondFloorLink = Main.class.getResource("images/02_thesecondfloor.png");
    static URL thirdFloorLink = Main.class.getResource("images/03_thethirdfloor.png");
    static URL LLOneLink = Main.class.getResource("images/00_thelowerlevel1.png");
    static URL LLTwoLink = Main.class.getResource("images/00_thelowerlevel2.png");

    public static ImageView lowerLevel2 = new ImageView(LLTwoLink.toExternalForm());
    public static ImageView lowerLevel1 = new ImageView(LLOneLink.toExternalForm());
    public static ImageView firstFloor = new ImageView(firstFloorLink.toExternalForm());
    public static ImageView secondFloor = new ImageView(secondFloorLink.toExternalForm());
    public static ImageView thirdFloor = new ImageView(thirdFloorLink.toExternalForm());

    String[] floorNames = {
            "Lower Level Two",
            "Lower Level One",
            "First Floor",
            "Second Floor",
            "Third Floor"
    };

    String[] nodeFloorNames = {
            "L2",
            "L1",
            "1",
            "2",
            "3"
    };

    private MapDatabase mapdb;
    ArrayList<Node> nodes;
    ArrayList<Edge> edges;
    ArrayList<LocationName> locationNames;
    ArrayList<Move> moves;

    List<Map<Integer, Node>> floorNodeMaps;
    Map<Integer, List<Integer>> edgeMap;
    Map<Integer, Node> nodeMap;

    int temp_node_id = -1;

    public MapStorage() throws SQLException, ClassNotFoundException {
        mapdb = new MapDatabase();
        nodes = mapdb.getNodes();
        edges = mapdb.getEdges();
        locationNames = mapdb.getLocationNames();
        moves = mapdb.getMoves();
        floorNodeMaps = new ArrayList<>(5);
        nodeMap = new HashMap<>();

        for (Node n : nodes) {
            nodeMap.put(n.getNodeID(), n);
        }

        for (int i = 0; i < 5; i++) {
            nodes = mapdb.getNodesByFloor(nodeFloorNames[i]);
            Map<Integer, Node> nodeMap = new HashMap<>();
            floorNodeMaps.add(nodeMap);
            for (Node n : nodes) {
                nodeMap.put(n.getNodeID(), n);
            }
        }

        for (Edge e : edges) {

        }
    }

    public Node getNodeByID(int nodeID) {
        return nodeMap.get(nodeID);
    }

    public Collection<Node> getNodesByFloor(int floor) {
        return floorNodeMaps.get(floor).values();
    }

    public List<Node> getNodesByType() {
        return null;
    }

    public Node addNode(int xCoord, int yCoord, String floorNum, String building) {
        Node n = new Node(temp_node_id, xCoord, yCoord, floorNum, building);
        temp_node_id--;
        Arrays.binarySearch(floorNames, "e");
    }

    private int getFloorNum(String floor) {
        for (String s : nodeFloorNames) {

        }
    }

    public static ImageView getLowerLevel2() {
        return lowerLevel2;
    }

    public static ImageView getLowerLevel1() {
        return lowerLevel1;
    }

    public static ImageView getFirstFloor() {
        return firstFloor;
    }

    public static ImageView getSecondFloor() {
        return secondFloor;
    }

    public static ImageView getThirdFloor() {
        return thirdFloor;
    }
}
