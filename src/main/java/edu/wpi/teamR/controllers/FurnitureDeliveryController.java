package edu.wpi.teamR.controllers;

import edu.wpi.teamR.navigation.Navigation;
import edu.wpi.teamR.navigation.Screen;
import edu.wpi.teamR.requestdb.RequestDatabase;
import edu.wpi.teamR.requestdb.RequestStatus;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.controlsfx.control.SearchableComboBox;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class FurnitureDeliveryController {

    @FXML Button submitButton;
    @FXML Button resetButton;
    @FXML Button cancelButton;
    @FXML MFXTextField nameField;
    @FXML MFXTextField staffMemberField;
    @FXML MFXTextField notesField;
    @FXML SearchableComboBox locationField;
    @FXML SearchableComboBox furnitureTypeField;

    @FXML public void initialize() {
        cancelButton.setOnMouseClicked(event -> Navigation.navigate(Screen.HOME));
        resetButton.setOnMouseClicked(event -> clear());
        submitButton.setOnMouseClicked(event -> {
            try {
                submit();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        furnitureTypeField.setValue("Select Furniture");
        locationField.setValue("Select Your Location");
    }

    public Timestamp CurrentDateTime(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println(dtf.format(now));
        return new Timestamp(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), now.getHour(),now.getMinute(),now.getSecond(),now.getNano());
    }

    @FXML
    public void clear() {
        nameField.clear();
        staffMemberField.clear();
        notesField.clear();

        furnitureTypeField.setValue("Select Furniture");
        locationField.setValue("Select Your Location");
    }

    @FXML
    public void submit() throws SQLException, ClassNotFoundException {
        String furnitureType = furnitureTypeField.getValue().toString();
        String location = locationField.getValue().toString();

        if (furnitureType == "Select Furniture") {
            furnitureType = "";
        }

        if (location == "Select Your Location") {
            location = "";
        }

        Navigation.navigate(Screen.HOME);
        int id = 0;
        RequestDatabase db = RequestDatabase.getInstance();

        ArrayList<MealRequest> foodList = db.getMealRequests();
        for(MealRequest foodRequest: foodList){
            if(id < foodRequest.getRequestID()){
                id = foodRequest.getRequestID();
            }
        }

        ArrayList<FurnitureRequest> furnList = db.getFurnitureRequests();
        for(FurnitureRequest furnitureRequest: furnList){
            if(id < furnitureRequest.getRequestID()){
                id = furnitureRequest.getRequestID();
            }
        }
        id++;
        try {
            db.addFurnitureRequest(nameField.getText(), location, staffMemberField.getText(), notesField.getText(), CurrentDateTime(), RequestStatus.Unstarted, furnitureType);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

}
