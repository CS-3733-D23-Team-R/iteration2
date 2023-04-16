package edu.wpi.teamR.controllers;

import edu.wpi.teamR.App;
import edu.wpi.teamR.navigation.Navigation;
import edu.wpi.teamR.navigation.Screen;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;

import java.io.IOException;

public class RootController {
  @FXML VBox bwhHome;
  @FXML VBox profileButton;
  @FXML VBox newRequestButton;
  @FXML VBox pendingRequestButton;
  @FXML VBox pathfindingButton;
  @FXML VBox helpButton;
  @FXML VBox logoutButton;
  @FXML VBox exitButton;
  @FXML Text flowerDelivery;
  @FXML Text furnitureDelivery;
  @FXML Text mealDelivery;

  @FXML VBox sidebarVBox;
  @FXML
  HBox rootHbox;

  @FXML
  ImageView profileIcon;
  @FXML
  Circle circle;

  PauseTransition transition;

  private static RootController instance;

  EventHandler<InputEvent> ssevent = evt -> transition.playFromStart();

  @FXML
  public void initialize() {
    instance = this;

    bwhHome.setOnMouseClicked(event -> navigate(Screen.HOME));
    profileButton.setOnMouseClicked(event -> navigate(Screen.SIGNAGE));
    newRequestButton.setOnMouseClicked(event -> openRequest());
    pendingRequestButton.setOnMouseClicked(event -> navigate(Screen.SORT_ORDERS));
    pathfindingButton.setOnMouseClicked(event -> navigate(Screen.MAP));
    exitButton.setOnMouseClicked(event -> Platform.exit());

    helpButton.setOnMouseClicked(
            event -> {
              try {
                help();
              } catch (IOException e) {
                e.printStackTrace();
              }
            });

    sidebarVBox.setVisible(false);
    sidebarVBox.setManaged(false);

    Duration delay = Duration.seconds(120);
    transition = new PauseTransition(delay);
    transition.setOnFinished(evt -> timeout());

    // restart transition on user interaction
    App.getPrimaryStage().addEventFilter(InputEvent.ANY, ssevent);

    transition.play();
  }

  @FXML
  private void help() throws IOException {
    PopOver helpPopup = new PopOver();
    final FXMLLoader loader =
            new FXMLLoader(getClass().getResource("/edu/wpi/teamR/views/Help.fxml"));
    Parent help = loader.load();
    helpPopup.setContentNode(help);
    helpPopup.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
    helpPopup.setAutoHide(true);
    helpPopup.show(helpButton);
  }
  @FXML
  private void openRequest(){
    if (sidebarVBox.isVisible()) {
      sidebarVBox.setVisible(false);
      sidebarVBox.setManaged(false);
    }
    else {
      sidebarVBox.setVisible(true);
      sidebarVBox.setManaged(true);
      flowerDelivery.setOnMouseClicked(event -> flowerRequest());
      furnitureDelivery.setOnMouseClicked(event -> furnitureRequest());
      mealDelivery.setOnMouseClicked(event -> mealRequest());
    }
  }

  @FXML private void mealRequest() {
    RequestController.requestType = new RequestTypeMeal();
    navigate(Screen.MEAL_REQUEST);
  }

  @FXML private void flowerRequest() {
    RequestController.requestType = new RequestTypeFlower();
    navigate(Screen.FLOWER_REQUEST);
  }

  @FXML private void furnitureRequest() {
    RequestController.requestType = new RequestTypeFurniture();
    navigate(Screen.FURNITURE_REQUEST);
  }

  /* This is a little buggy and could be worked on more. */
  private void timeout() {
      rootHbox.setVisible(false);
      rootHbox.setManaged(false);
      Navigation.navigate(Screen.SCREENSAVER);
      App.getPrimaryStage().removeEventFilter(InputEvent.ANY, ssevent);
  }

  public static RootController getInstance() {
    return instance;
  }

  private void navigate(Screen screen) {
    sidebarVBox.setVisible(false);
    sidebarVBox.setManaged(false);
    Navigation.navigate(screen);
  }

  public void showSidebar() {
    rootHbox.setVisible(true);
    rootHbox.setManaged(true);
    App.getPrimaryStage().addEventFilter(InputEvent.ANY, ssevent);
  }
}
