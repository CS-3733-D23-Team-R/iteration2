package edu.wpi.teamR.controllers.mapeditor;

import edu.wpi.teamR.App;
import edu.wpi.teamR.ItemNotFoundException;
import edu.wpi.teamR.mapdb.LocationName;
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

public class NewLocationPopupController {
    MapUpdater mapUpdater;
    MapDatabase mapDB;

    @FXML
    TextField longField;
    @FXML TextField shortField;
    @FXML
    ComboBox<String> typeBox;
    @FXML
    Button addButton;

    ObservableList<String> locationTypes =
            FXCollections.observableArrayList("HALL", "LABS", "ELEV", "SERV", "CONF", "STAI", "INFO", "REST", "DEPT", "BATH", "EXIT", "RETL");

    public void initialize() {
        this.mapDB = App.getMapData().getMapdb();

        typeBox.setItems(locationTypes);
        addButton.setOnAction(event -> {
            try {
                createNewLocation();
                close((Stage)addButton.getScene().getWindow());
            } catch (SQLException | ItemNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    public void close(Stage primaryStage) throws SQLException {
        primaryStage.close();
    }

    public void createNewLocation() throws SQLException, ItemNotFoundException {
        LocationName loc;
        try {
            loc = mapDB.getLocationNameByLongName(longField.getText());
        } catch (ItemNotFoundException e) {
            loc = null;
        }
        if (loc == null) {
            mapUpdater.addLocationName(longField.getText(), shortField.getText(), typeBox.getValue());
        }
    }

    public void setMapUpdater(MapUpdater updater) {
        mapUpdater = updater;
    }

    public void clearFields() {
        longField.clear();
        shortField.clear();
        typeBox.setValue(null);
    }
}
