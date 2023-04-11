package edu.wpi.teamR.controllers;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.controlsfx.control.SearchableComboBox;

public class MealDeliveryController {

    @FXML Button submitButton;
    @FXML Button resetButton;
    @FXML Button cancelButton;
    @FXML MFXTextField nameField;
    @FXML MFXTextField staffMemberField;
    @FXML MFXTextField notesField;
    @FXML SearchableComboBox locationField;
    @FXML SearchableComboBox mealTypeField;

    @FXML public void initialize() {

    }

}
