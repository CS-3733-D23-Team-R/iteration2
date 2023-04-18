package edu.wpi.teamR.controllers.mapeditor;

import edu.wpi.teamR.mapdb.MapDatabase;
import edu.wpi.teamR.mapdb.update.MapUpdater;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class NewNodePopupController {
    MapUpdater mapUpdater;

    @FXML
    TextField xField;
    @FXML TextField yField;
    @FXML
    ComboBox<String> floorCB;
    @FXML
    ComboBox<String> buildingCB;
    @FXML
    Button addButton;

    ObservableList<String> floors =
            FXCollections.observableArrayList("L2",
                    "L1",
                    "1",
                    "2",
                    "3");
    ObservableList<String> buildingNames =
            FXCollections.observableArrayList("15 Francis", "45 Francis", "BTM", "Shapiro", "Tower");

    public void initialize() {
        floorCB.setItems(floors);
        buildingCB.setItems(buildingNames);
        addButton.setOnAction(event -> {
            try {
                createNewNode();
                close((Stage)addButton.getScene().getWindow());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    public void close(Stage primaryStage) throws SQLException {
        primaryStage.close();
    }

    public void createNewNode() throws SQLException {
        mapUpdater.addNode(Integer.parseInt(xField.getText()), Integer.parseInt(xField.getText()), floorCB.getValue(), buildingCB.getValue());
    }

    public void setUpdater(MapUpdater updater) {
        mapUpdater = updater;
    }
}
