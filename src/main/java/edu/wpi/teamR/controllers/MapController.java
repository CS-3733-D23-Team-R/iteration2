package edu.wpi.teamR.controllers;

import edu.wpi.teamR.ItemNotFoundException;
import edu.wpi.teamR.App;
import edu.wpi.teamR.mapdb.MapDatabase;
import edu.wpi.teamR.navigation.Navigation;
import edu.wpi.teamR.navigation.Screen;
import io.github.palexdev.materialfx.controls.MFXCheckbox;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javafx.animation.Interpolator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import net.kurobako.gesturefx.*;
import edu.wpi.teamR.pathfinding.*;
import edu.wpi.teamR.mapdb.*;
import org.controlsfx.control.SearchableComboBox;

import javax.xml.stream.Location;

public class MapController {
    @FXML
    Button resetButton;
    @FXML Button searchButton;
    @FXML Button floorUpButton;
    @FXML Button floorDownButton;
    @FXML MFXCheckbox accessibleCheckbox;

    @FXML
    SearchableComboBox<String> startField;
    @FXML
    SearchableComboBox<String> endField;

    @FXML BorderPane borderPane;
    @FXML AnchorPane anchorPane;

    @FXML GesturePane gesturePane;

    @FXML Text floorText;
    @FXML Button clearButton;
    @FXML MFXCheckbox textCheckbox;

    @FXML MFXCheckbox locationNamesCheckbox;
    AnchorPane[] locationPanes = new AnchorPane[5];

    ImageView imageView;

    private Map<Integer, ImageView> floorMaps = new HashMap<>();
    int currentFloor = 2;

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

    AnchorPane[] paths = new AnchorPane[5];

    private AnchorPane mapPane = new AnchorPane();

    HashMap<String, Integer> floorNamesMap = new HashMap<String, Integer>();

    private MapDatabase mapdb;
    ArrayList<Node> nodes;
    ArrayList<Edge> edges;
    ArrayList<LocationName> locationNames;
    ArrayList<Move> moves;

    @FXML
    public void initialize() throws Exception {
        mapdb = App.getMapData().getMapdb();
        nodes = App.getMapData().getNodes();
        edges = App.getMapData().getEdges();
        locationNames = App.getMapData().getLocationNames();
        moves = App.getMapData().getMoves();

        floorMaps.put(0, App.getMapData().getLowerLevel2());
        floorMaps.put(1, App.getMapData().getLowerLevel1());
        floorMaps.put(2, App.getMapData().getFirstFloor());
        floorMaps.put(3, App.getMapData().getSecondFloor());
        floorMaps.put(4, App.getMapData().getThirdFloor());

        for (int i = 0; i < 5; i++) {
            paths[i] = new AnchorPane();
            locationPanes[i] = new AnchorPane();
            floorNamesMap.put(nodeFloorNames[i], i);
            displayLocationNames(i);
        }

        gesturePane.setContent(mapPane);
        mapPane.getChildren().add(floorMaps.get(currentFloor));
        gesturePane.setMinScale(0.25);
        gesturePane.setMaxScale(2);
        resetButton.setOnMouseClicked(event -> reset());
        floorDownButton.setOnMouseClicked(event -> displayFloorDown());
        floorUpButton.setOnMouseClicked(event -> displayFloorUp());
        clearButton.setOnMouseClicked(event -> clearPath());
        searchButton.setOnMouseClicked(event -> {
            try {
                search();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        floorText.setText(floorNames[currentFloor]);
        startField.setValue("Select Start");
        endField.setValue("Select End");

        reset();

        setChoiceboxes();

        locationNamesCheckbox.setOnAction(event -> {
            if (locationNamesCheckbox.isSelected()) {
                mapPane.getChildren().add(locationPanes[currentFloor]);
            } else {
                mapPane.getChildren().remove(locationPanes[currentFloor]);
            }
        });
    }

    // Reset to original zoom
    public void reset() {
        // zoom to 1x
        gesturePane.zoomTo(0.25, 0.25, new Point2D(2500, 1700));
        gesturePane.zoomTo(0.25, 0.25, new Point2D(2500, 1700));
        gesturePane.centreOn(new Point2D(2500, 1700));
    }

    public void clearPath() {
        paths[currentFloor].getChildren().clear();
        mapPane.getChildren().remove(paths[currentFloor]);
        for (int i = 0; i < 5; i++) {
            paths[i] = new AnchorPane();
        }
    }

    // zoom into a desired location
    // TODO fix zoomTo error
    public void animateZoomTo(int x, int y, int time) {
        // gesturePane.animate(Duration.millis(time)).zoomTo(1);
        // animate with some options
        gesturePane
                .animate(Duration.millis(time))
                .interpolateWith(Interpolator.EASE_BOTH)
                .afterFinished(() -> System.out.println("Done!"))
                .centreOn(new Point2D(x, y));
    }

    public void search() throws Exception, ItemNotFoundException {
    /*TODO
    take info from fields
    calculate route
    find spread of nodes on current floor
    animateZoom to show all nodes on this floor
    create path between nodes on ALL floors
    create/display textual path? (would have to add spot to display)
     */
        String start = startField.getValue();
        String end = endField.getValue();
        Boolean isAccessible = accessibleCheckbox.isPressed();
        displayPath(start, end, isAccessible);
    }

    public void displayFloorUp() {
        if (currentFloor < 4) {
            currentFloor++;
            imageView = floorMaps.get(currentFloor);
            mapPane.getChildren().clear();
            mapPane.getChildren().add(imageView);
            mapPane.getChildren().add(paths[currentFloor]);
            floorText.setText(floorNames[currentFloor]);
            reset();
        }
    }

    public void displayFloorDown() {
        if (currentFloor > 0) {
            currentFloor--;
            imageView = floorMaps.get(currentFloor);
            mapPane.getChildren().clear();
            mapPane.getChildren().add(imageView);
            mapPane.getChildren().add(paths[currentFloor]);
            floorText.setText(floorNames[currentFloor]);
            reset();
        }
    }

    public void displayFloorNum(int floorNum) {
        if (floorNum < 4) {
            currentFloor = floorNum;
            imageView = floorMaps.get(currentFloor);
            mapPane.getChildren().clear();
            mapPane.getChildren().add(imageView);
            mapPane.getChildren().add(paths[currentFloor]);
            floorText.setText(floorNames[currentFloor]);
            reset();
        }
    }

    public void displayPath(String startLocation, String endLocation, Boolean accessible) throws Exception, ItemNotFoundException {
        clearPath();
        mapPane.getChildren().add(paths[currentFloor]);

        int startID = idFromName(startLocation);
        int endID = idFromName(endLocation);

        Pathfinder pathfinder = new Pathfinder(mapdb);
        Path mapPath = pathfinder.aStarPath(startID, endID, accessible);
        ArrayList<Integer> currentPath = mapPath.getPath();

        Node startNode = mapdb.getNodeByID(startID);
        Node endNode = mapdb.getNodeByID(endID);

        if (startNode.getFloorNum() != nodeFloorNames[currentFloor]){
            displayFloorNum(floorNamesMap.get(startNode.getFloorNum()));
        }

        createCircle(startNode, currentFloor, startField);

        int drawFloor = currentFloor;
        for (int i = 0; i < mapPath.getPath().size() - 1; i++) {
            Node n1 = mapdb.getNodeByID(mapPath.getPath().get(i));
            Node n2 = mapdb.getNodeByID(mapPath.getPath().get(i + 1));
            if (n1.getFloorNum().equals(nodeFloorNames[drawFloor]) && n2.getFloorNum().equals(nodeFloorNames[drawFloor])) {
                Line l1 = new Line(n1.getXCoord(), n1.getYCoord(), n2.getXCoord(), n2.getYCoord());
                paths[drawFloor].getChildren().add(l1);
            }
            else {
                drawFloor = floorNamesMap.get(n2.getFloorNum());
            }
        }
        createCircle(endNode, drawFloor, endField);

        gesturePane.zoomTo(1, 1, new Point2D(startNode.getXCoord(), startNode.getYCoord()));
        gesturePane.centreOn(new Point2D(startNode.getXCoord(), startNode.getYCoord()));
    }

    private void createCircle(Node endNode, int drawFloor, SearchableComboBox<String> endField) {
        Circle end = new Circle(endNode.getXCoord(), endNode.getYCoord(), 5, Color.RED);
        Text endText = new Text(endField.getValue());
        endText.setX(endNode.getXCoord() + 10);
        endText.setY(endNode.getYCoord());
        endText.setFill(Color.RED);
        paths[drawFloor].getChildren().add(end);
        paths[drawFloor].getChildren().add(endText);
    }

    private int idFromName(String longname) throws SQLException, ItemNotFoundException {
        return mapdb.getLatestMoveByLocationName(longname).getNodeID();
    }

    void setChoiceboxes(){
        ArrayList<LocationName> locationNodes = locationNames;
        ArrayList<String> names = new ArrayList<String>();
        for (LocationName l: locationNodes) {
            if(!l.getLongName().contains("Hall")) {
                names.add(l.getLongName());
            }
        }
        ObservableList<String> choices = FXCollections.observableArrayList(names);
        startField.setItems(choices);
        endField.setItems(choices);
    }

    public void displayLocationNames(int floor) throws SQLException {
        if (floor < 4) {
            String f = nodeFloorNames[floor];
            ArrayList<MapLocation> locs = mapdb.getMapLocationsByFloor(f);
            if (locs.size() > 0) {
                for (MapLocation m: locs) {
                    Text t = new Text();
                    Node n = m.getNode();
                    t.setText(m.getLocationNames().get(0).getShortName()); //m.getLocationNames().get(0).getShortName()
                    t.setX(n.getXCoord() + 10);
                    t.setY(n.getYCoord());

                    locationPanes[floor].getChildren().add(t);
                }
            }
        }
    }
}