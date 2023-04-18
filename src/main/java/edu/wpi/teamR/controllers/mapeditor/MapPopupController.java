package edu.wpi.teamR.controllers.mapeditor;

import edu.wpi.teamR.ItemNotFoundException;
import edu.wpi.teamR.mapdb.*;
import edu.wpi.teamR.mapdb.update.MapUpdater;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class MapPopupController {
    @FXML
    Text nodeField;
    @FXML
    TextField locField;
    @FXML
    DatePicker moveDatePicker;

    Node node;
    Move move;
    MapUpdater mapUpdater;
    LocationName locationName;
    @FXML
    Button deleteButton;
    @FXML
    public void initialize() {
        moveDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            try {
                handleDatePickerChange(newValue);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        locField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                updateLocationName();
            } catch (SQLException | ItemNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        moveDatePicker.setFocusTraversable(false);
        locField.setFocusTraversable(false);

        deleteButton.setOnAction(event -> {
            try {
                delete();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    public void close(Stage primaryStage) {
        primaryStage.close();
    }

    @FXML public void start(Stage primaryStage) {
        primaryStage.show();
    }

    public void showNodeInformation(MapUpdater updater, Node n, LocationName l, Move m) {
        node = n;
        move = m;
        mapUpdater = updater;
        locationName = l;
        nodeField.setText(Integer.toString(n.getNodeID()));
        locField.setText(l.getShortName());

        Date moveDate = m.getMoveDate();
        LocalDate date = moveDate.toLocalDate();
        moveDatePicker.setValue(date);
    }

    private void handleDatePickerChange(LocalDate newValue) throws SQLException {
        Date sqlDate = Date.valueOf(newValue);
        int nodeID = move.getNodeID();
        String longName = move.getLongName();
        mapUpdater.addMove(nodeID, longName, sqlDate);
    }

    private void updateLocationName() throws SQLException, ItemNotFoundException {
        mapUpdater.modifyLocationNameShortName(locationName.getLongName(), locField.getText());
    }

    private void delete() throws SQLException {
        mapUpdater.deleteEdgesByNode(node.getNodeID());
        mapUpdater.deleteMovesByNode(move.getNodeID());
        mapUpdater.deleteNode(node.getNodeID());
    }
}
