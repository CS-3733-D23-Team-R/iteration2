package edu.wpi.teamR.controllers;

import edu.wpi.teamR.ItemNotFoundException;
import edu.wpi.teamR.Main;
import edu.wpi.teamR.csv.CSVParameterException;
import edu.wpi.teamR.csv.CSVWriter;
import edu.wpi.teamR.pathfinding.Line;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import edu.wpi.teamR.mapdb.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.kurobako.gesturefx.GesturePane;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javafx.geometry.Point2D;
import org.controlsfx.control.PopOver;

public class MapEditorController {
    @FXML
    Button importCSVButton;
    @FXML Button exportCSVButton;
    @FXML
    Button editButton;
    @FXML Button saveButton;
    @FXML Button newLocationButton;
    @FXML Button newNodeButton;
    @FXML Button newEdgeButton;
    @FXML Button newMoveButton;
    @FXML Button redrawButton;

    static File selectedFile;
    static File selectedDirectory;
    FileChooser fileChooser;

    DirectoryChooser directoryChooser;

    @FXML
    BorderPane borderPane;
    @FXML AnchorPane anchorPane;

    @FXML
    GesturePane gesturePane;

    @FXML
    ComboBox<String> floorComboBox;
    @FXML
    ComboBox<String> tableComboBox;
    @FXML
    MFXCheckbox locationCheckbox;
    ObservableList<String> DAOType =
            FXCollections.observableArrayList("Node", "Edge", "LocationName", "Moves");
    ObservableList<String> floors =
            FXCollections.observableArrayList("Lower Level Two",
                    "Lower Level One",
                    "First Floor",
                    "Second Floor",
                    "Third Floor");

    URL firstFloorLink = Main.class.getResource("images/01_thefirstfloor.png");
    URL secondFloorLink = Main.class.getResource("images/02_thesecondfloor.png");
    URL thirdFloorLink = Main.class.getResource("images/03_thethirdfloor.png");
    URL LLOneLink = Main.class.getResource("images/00_thelowerlevel1.png");
    URL LLTwoLink = Main.class.getResource("images/00_thelowerlevel2.png");

    ImageView imageView;
    int currentFloor = 2;

    URL[] linkArray = {
            LLTwoLink, LLOneLink, firstFloorLink, secondFloorLink, thirdFloorLink,
    };

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

    AnchorPane[] nodePanes = new AnchorPane[5];
    AnchorPane[] locationPanes = new AnchorPane[5];

    private AnchorPane mapPane = new AnchorPane();

    HashMap<String, Integer> floorNamesMap = new HashMap<>();

    private MapDatabase mapdb;
    Node selectedNode;
    boolean drawEdgesMode = false;
    @FXML
    HBox edgeHBox;

    @FXML
    public void initialize() throws SQLException, ClassNotFoundException, ItemNotFoundException {
        for (int i = 0; i < 5; i++) {
            nodePanes[i] = new AnchorPane();
            locationPanes[i] = new AnchorPane();
            floorNamesMap.put(nodeFloorNames[i], i);
            floorNamesMap.put(floorNames[i], i);
        }

        floorComboBox.setItems(floors);
        tableComboBox.setItems(DAOType);
        importCSVButton.setOnAction(event -> {
            try {
                importCSV();
            } catch (IOException | NoSuchMethodException | IllegalAccessException | InstantiationException |
                     InvocationTargetException | CSVParameterException | SQLException | ItemNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        exportCSVButton.setOnAction(event -> {
            try {
                export();
            } catch (IOException | SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });

        editButton.setOnAction(event -> gesturePane.setGestureEnabled(false));
        saveButton.setOnAction(event -> gesturePane.setGestureEnabled(true));

        floorComboBox.setOnAction(event -> {
            try {
                changeFloor(floorNamesMap.get(floorComboBox.getValue()));
            } catch (SQLException | ItemNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });

        newLocationButton.setOnAction(event -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/wpi/teamR/views/NewLocationPopup.fxml"));
            try {
                Parent popupRoot = loader.load();
                NewLocationPopupController popupController = loader.getController();
                popupController.setMapDB(mapdb); // Pass the object to the popup controller

                // Create a new stage for the popup
                Stage popupStage = new Stage();
                popupStage.initModality(Modality.APPLICATION_MODAL);
                popupStage.setTitle("Create New Location");
                popupStage.setScene(new Scene(popupRoot, 400, 200));
                popupStage.showAndWait(); // Show the popup and wait for it to be closed
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        newNodeButton.setOnAction(event -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/wpi/teamR/views/NewNodePopup.fxml"));
            try {
                Parent popupRoot = loader.load();
                NewNodePopupController popupController = loader.getController();
                popupController.setMapDB(mapdb); // Pass the object to the popup controller

                // Create a new stage for the popup
                Stage popupStage = new Stage();
                popupStage.initModality(Modality.APPLICATION_MODAL);
                popupStage.setTitle("Create New Node");
                popupStage.setScene(new Scene(popupRoot, 400, 200));
                popupStage.showAndWait(); // Show the popup and wait for it to be closed
                redraw();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException | ItemNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        edgeDialog(false);
        newEdgeButton.setOnAction(event -> edgeDialog(true));

        redrawButton.setOnAction(event -> {
            try {
                redraw();
            } catch (SQLException | ItemNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        locationCheckbox.setOnAction(event -> {
            if (locationCheckbox.isSelected()) {
                mapPane.getChildren().remove(nodePanes[currentFloor]);
                mapPane.getChildren().add(locationPanes[currentFloor]);
                mapPane.getChildren().add(nodePanes[currentFloor]);
            } else {
                mapPane.getChildren().remove(locationPanes[currentFloor]);
            }
        });


        imageView = new ImageView(linkArray[currentFloor].toExternalForm());
        gesturePane.setContent(mapPane);
        mapPane.getChildren().add(imageView);
        gesturePane.setMinScale(0.25);
        gesturePane.setMaxScale(2);

        try {
            mapdb = new MapDatabase();
            displayEdgesByFloor(currentFloor);
            displayNodesByFloor(currentFloor);
        }
        catch (ItemNotFoundException e) {
            e.printStackTrace();
        }
        reset();
    }

    private void edgeDialog(boolean setting) {
        drawEdgesMode=setting;
        edgeHBox.setVisible(setting);
        edgeHBox.setManaged(setting);
    }

    @FXML
    public void close(Stage primaryStage) throws SQLException {
        //mapdb.closeConnection();
    }

    public void changeFloor(int floorNum) throws SQLException, ItemNotFoundException {
        if (floorNum < 4) {
            currentFloor = floorNum;
            imageView = new ImageView(linkArray[currentFloor].toExternalForm());
            mapPane.getChildren().clear();
            mapPane.getChildren().add(imageView);
            reset();
            redraw();
        }
    }

    public void reset() {
        // zoom to 1x
        gesturePane.zoomTo(0.25, 0.25, new Point2D(2500, 1700));
        gesturePane.zoomTo(0.25, 0.25, new Point2D(2500, 1700));
        gesturePane.centreOn(new Point2D(2500, 1700));
    }


    public void displayNodesByFloor(int floor) throws SQLException, ItemNotFoundException {
        String f = nodeFloorNames[floor];
        mapPane.getChildren().add(locationPanes[currentFloor]);
        mapPane.getChildren().add(nodePanes[floor]);

        ArrayList<MapLocation> mapLocations = mapdb.getMapLocationsByFloor(f);
        for (MapLocation l: mapLocations) {
            drawNode(l, floor);
        }
    }

    public void displayEdgesByFloor(int floor) throws SQLException {
        if (floor < 4) {
            String f = nodeFloorNames[floor];
            ArrayList<Edge> edges = mapdb.getEdgesByFloor(f);
            for (Edge e: edges) {
                Node n1 = mapdb.getNodeByID(e.getStartNode());
                Node n2 = mapdb.getNodeByID(e.getEndNode());
                Line l1 = new Line(n1.getXCoord(), n1.getYCoord(), n2.getXCoord(), n2.getYCoord());

                l1.setOnMouseClicked(event -> {
                    if (!gesturePane.isGestureEnabled()) {
                        nodePanes[floor].getChildren().remove(l1);
                        try {
                            mapdb.deleteEdge(n1.getNodeID(), n2.getNodeID());
                            redraw();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        } catch (ItemNotFoundException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                });

                nodePanes[floor].getChildren().add(l1);
            }
        }
    }

    public void importCSV () throws IOException, CSVParameterException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, SQLException, ItemNotFoundException {
        openFile();
        String choice = tableComboBox.getValue();
        switch (choice) {
            case "Node" -> {
                mapdb.readCSV(selectedFile.getAbsolutePath(), Node.class);
            }
            case "Edge" -> {
                mapdb.readCSV(selectedFile.getAbsolutePath(), Edge.class);
            }
            case "LocationName" -> {
                mapdb.readCSV(selectedFile.getAbsolutePath(), LocationName.class);
            }
            case "Moves" -> {
                mapdb.readCSV(selectedFile.getAbsolutePath(), Move.class);
            }
        }
        tableComboBox.getSelectionModel().clearSelection();
        tableComboBox.setValue(null);
    }

    public void openFile() {
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setTitle("Open File");
        selectedFile = fileChooser.showOpenDialog(importCSVButton.getScene().getWindow());
    }

    public void chooseDirectory() {
        directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose Directory");
        selectedDirectory = directoryChooser.showDialog(exportCSVButton.getScene().getWindow());
    }

    public void export() throws IOException, SQLException {
        String choice = tableComboBox.getValue();
        chooseDirectory();
        switch (choice) {
            case "Node" -> {
                CSVWriter<Node> nodeCSVWriter = new CSVWriter<>();
                nodeCSVWriter.writeCSV(selectedDirectory.getAbsolutePath() + "/Nodes.csv", mapdb.getNodes());
            }
            case "Edge" -> {
                CSVWriter<Edge> edgeCSVWriter = new CSVWriter<>();
                edgeCSVWriter.writeCSV(selectedDirectory.getAbsolutePath() + "/Edges.csv", mapdb.getEdges());
            }
            case "LocationName" -> {
                CSVWriter<LocationName> locationCSVWriter = new CSVWriter<>();
                locationCSVWriter.writeCSV(selectedDirectory.getAbsolutePath() + "/LocationNames.csv", mapdb.getLocationNames());
            }
            case "Moves" -> {
                CSVWriter<Move> moveCSVWriter = new CSVWriter<>();
                moveCSVWriter.writeCSV(selectedDirectory.getAbsolutePath() + "/Moves.csv", mapdb.getMoves());
            }
        }
        selectedDirectory = null;
        tableComboBox.getSelectionModel().clearSelection();
        tableComboBox.setValue(null);
    }

    public void displayLocationNames(int floor) throws SQLException {
        if (floor < 4) {
            String f = nodeFloorNames[floor];
            ArrayList<MapLocation> locs = mapdb.getMapLocationsByFloor(f);
            for (MapLocation m: locs) {
                Text t = new Text();
                Node n = m.getNode();
                t.setText("hi");
                t.setX(n.getXCoord() + 10);
                t.setY(n.getYCoord());

                locationPanes[floor].getChildren().add(t);
            }
        }
    }

    public void drawNode(MapLocation l, int floor) throws SQLException, ItemNotFoundException {
        if (floor < 4) {
            ArrayList<LocationName> ln = l.getLocationNames();
            if (!ln.get(0).getNodeType().equals("HALL")) {
                Node n = l.getNode();
                Move m = mapdb.getLatestMoveByLocationName(ln.get(0).getLongName());

                AtomicReference<Point2D> point = new AtomicReference<>(new Point2D(n.getXCoord(), n.getYCoord()));
                Circle c = new Circle(point.get().getX(), point.get().getY(), 5, Color.RED);
                nodePanes[floor].getChildren().add(c);

                AtomicReference<Point2D> edgePoint = new AtomicReference<>(new Point2D(n.getXCoord(), n.getYCoord()));

                AtomicBoolean dragging = new AtomicBoolean(false);

                c.setOnMouseClicked(event -> {
                    if (!dragging.get()) {
                        if (drawEdgesMode) {
                            if (selectedNode == null) {
                                selectedNode = n;
                            } else {
                                Line l1 = new Line(selectedNode.getXCoord(), selectedNode.getYCoord(), n.getXCoord(), n.getYCoord());
                                try {
                                    mapdb.addEdge(selectedNode.getNodeID(), n.getNodeID());
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                                nodePanes[floor].getChildren().add(l1);
                                selectedNode = null;
                                edgeDialog(false);
                            }
                        } else {
                            // Create and configure the PopOver
                            PopOver popOver = new PopOver();
                            final FXMLLoader loader =
                                    new FXMLLoader(getClass().getResource("/edu/wpi/teamR/views/MapPopup.fxml"));
                            Parent popup;
                            try {
                                popup = loader.load();
                            } catch (IOException e) {
                                e.printStackTrace();
                                throw new RuntimeException(e);
                            }
                            MapPopupController controller = loader.getController();
                            controller.showNodeInformation(mapdb, n, ln.get(0), m);

                            popOver.setContentNode(popup);
                            popOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
                            popOver.setAutoHide(true);
                            popOver.show(c);
                        }
                    }
                });
                c.setOnMouseDragged(dragEvent -> {
                    if (!gesturePane.isGestureEnabled()) {
                        dragging.set(true);
                        int deltaX = (int) (dragEvent.getX() - point.get().getX());
                        int deltaY = (int) (dragEvent.getY() - point.get().getY());
                        System.out.println(deltaX + " " + deltaY);
                        point.set(new Point2D(dragEvent.getX(), dragEvent.getY()));
                        c.setCenterX(point.get().getX());
                        c.setCenterY(point.get().getY());
                    }
                });
                c.setOnMouseReleased(dragEvent -> {
                    if (!gesturePane.isGestureEnabled()) {
                        try {
                            mapdb.modifyCoords(n.getNodeID(), (int) point.get().getX(), (int) point.get().getY());
                            redraw();
                        } catch (SQLException | ItemNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                dragging.set(false);

                Text t = new Text(ln.get(0).getShortName());
                t.setX(point.get().getX() + 10);
                t.setY(point.get().getY());
                t.setFill(Color.RED);
                locationPanes[floor].getChildren().add(t);
                locationCheckbox.setSelected(true);
            }
        }
    }

    public void redraw() throws SQLException, ItemNotFoundException {
        locationPanes[currentFloor].getChildren().clear();
        nodePanes[currentFloor].getChildren().clear();
        mapPane.getChildren().remove(nodePanes[currentFloor]);
        mapPane.getChildren().remove(locationPanes[currentFloor]);
        displayEdgesByFloor(currentFloor);
        displayNodesByFloor(currentFloor);
    }
}
