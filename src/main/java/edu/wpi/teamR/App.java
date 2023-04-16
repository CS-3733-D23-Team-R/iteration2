package edu.wpi.teamR;

import edu.wpi.teamR.navigation.Navigation;
import edu.wpi.teamR.navigation.Screen;
import java.io.IOException;
import java.sql.SQLException;

import edu.wpi.teamR.pathfinding.MapStorage;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App extends Application {

  @Setter @Getter private static Stage primaryStage;
  @Setter @Getter private static BorderPane rootPane;
  @Getter
  static MapStorage mapData;

  @Override
  public void init() {
    log.info("Starting Up");
  }

  @Override
  public void start(Stage primaryStage) throws IOException, SQLException, ClassNotFoundException {
    /* primaryStage is generally only used if one of your components require the stage to display */
    App.primaryStage = primaryStage;
    primaryStage.setTitle("Team R Hospital Application");
    Image icon = new Image(getClass().getResourceAsStream("images/bwhappicon.png"));
    primaryStage.getIcons().add(icon);

    final FXMLLoader loader = new FXMLLoader(App.class.getResource("views/Root.fxml"));
    final BorderPane root = loader.load();

    App.rootPane = root;
    App.mapData = new MapStorage();

    final Scene scene = new Scene(root);
    primaryStage.setScene(scene);

    primaryStage.getScene().getStylesheets().add("https://fonts.googleapis.com/css2?family=Lato:wght@700&family=Nunito+Sans&family=Quicksand&family=Roboto:wght@300;400&display=swap");

    primaryStage.setMaximized(true);
    primaryStage.show();

    Navigation.navigate(Screen.HOME);
  }

  @Override
  public void stop() {
    log.info("Shutting Down");
  }
}
