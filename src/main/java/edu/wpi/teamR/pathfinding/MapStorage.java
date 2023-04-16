package edu.wpi.teamR.pathfinding;

import edu.wpi.teamR.Main;
import edu.wpi.teamR.mapdb.*;
import javafx.scene.image.ImageView;
import lombok.Getter;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;

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

    private MapDatabase mapdb;
    ArrayList<Node> nodes;
    ArrayList<Edge> edges;
    ArrayList<LocationName> locationNames;
    ArrayList<Move> moves;

    public MapStorage() throws SQLException, ClassNotFoundException {
        mapdb = new MapDatabase();
        nodes = mapdb.getNodes();
        edges = mapdb.getEdges();
        locationNames = mapdb.getLocationNames();
        moves = mapdb.getMoves();
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
