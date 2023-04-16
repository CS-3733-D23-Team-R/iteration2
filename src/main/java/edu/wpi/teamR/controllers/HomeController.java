package edu.wpi.teamR.controllers;

import edu.wpi.teamR.navigation.Navigation;
import edu.wpi.teamR.navigation.Screen;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.*;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HomeController {

  @FXML BorderPane borderPane;
  @FXML Button loginButton;
  @FXML
  Text time;

  private static Parent root;

  @FXML
  public void initialize() {
    loginButton.setOnAction(event -> Navigation.navigate(Screen.MAP_EDITOR));

    LocalDate date = LocalDate.now();
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
    String formattedDate = date.format(dateTimeFormatter);

    Timeline timeline =
            new Timeline(
                    new KeyFrame(
                            Duration.seconds(0),
                            event -> {
                              LocalDateTime currentTime = LocalDateTime.now();
                              DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm:ss a");
                              String formattedTime = currentTime.format(timeFormatter);
                              String formattedDateTime = formattedTime + ", " + formattedDate;
                              time.setText(formattedDateTime);
                            }),
                    new KeyFrame(Duration.seconds(1)));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
  }
}
