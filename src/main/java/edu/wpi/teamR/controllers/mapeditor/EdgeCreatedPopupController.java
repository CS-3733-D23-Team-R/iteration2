package edu.wpi.teamR.controllers.mapeditor;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.SQLException;

public class EdgeCreatedPopupController {
    @FXML
    Button okButton;

    @FXML Text startNodeText;
    @FXML
    Text endNodeText;
    public void initialize() {
        okButton.setOnAction(event -> close((Stage)okButton.getScene().getWindow()));
    }

    @FXML
    public void close(Stage primaryStage) {
        primaryStage.close();
    }

    public void setText(int startID, int endID) {
        startNodeText.setText(Integer.toString(startID));
        endNodeText.setText(Integer.toString(endID));
    }
}
