package edu.wpi.teamR.controllers;

import edu.wpi.teamR.App;
import edu.wpi.teamR.Main;
import edu.wpi.teamR.navigation.Navigation;
import edu.wpi.teamR.navigation.Screen;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

import java.net.URL;

public class ScreensaverController {
    public Timeline timeline;

    private static final int WIDTH = 100;
    private static final int HEIGHT = 150;

    @FXML BorderPane root;
    @FXML
    ImageView bwh;
    @FXML
    AnchorPane anchorPane;

    private URL[] images;
    private int currentImageIndex = 0;
    private int offsetX = 5;
    private int offsetY = 5;

    public void initialize() {
        images = new URL[]{
                Main.class.getResource("images/screensaver/bwh_white.png"),
                Main.class.getResource("images/screensaver/bwh_yellow.png"),
                Main.class.getResource("images/screensaver/bwh_pink.png"),
                Main.class.getResource("images/screensaver/bwh_cyan.png"),
                Main.class.getResource("images/screensaver/bwh_green.png")};

        bwh.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                timeline = new Timeline(new KeyFrame(Duration.millis(60), e -> moveLogo()));
                timeline.setCycleCount(Animation.INDEFINITE);
                timeline.play();
            }
            else {
                timeline.stop();
            }
        });

        anchorPane.setOnMouseClicked(evt -> goHome());
    }

    private void moveLogo() {
        // movement
        bwh.setTranslateX(bwh.getTranslateX() + offsetX);
        bwh.setTranslateY(bwh.getTranslateY() + offsetY);

        // handle collisions with AnchorPane bounds
        if (bwh.getTranslateX() <= 0 || bwh.getTranslateX() + WIDTH >= anchorPane.getWidth()) {
            offsetX *= -1;
            changeImage();
        }
        if (bwh.getTranslateY() <= 0 || bwh.getTranslateY() + HEIGHT >= anchorPane.getHeight()) {
            offsetY *= -1;
            changeImage();
        }
    }

    private void changeImage() {
        currentImageIndex = (currentImageIndex + 1) % images.length;
        bwh.setImage(new Image(images[currentImageIndex].toExternalForm()));
    }

    private void goHome(){
        Navigation.navigate(Screen.HOME);
        RootController rootController = RootController.getInstance();
        rootController.showSidebar();
    }
}
