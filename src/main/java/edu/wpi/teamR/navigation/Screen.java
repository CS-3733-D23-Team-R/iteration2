package edu.wpi.teamR.navigation;

public enum Screen {
  ROOT("views/Root.fxml"),
  HOME("views/Home.fxml"),
  SIGNAGE("views/Signage.fxml"),
  MEAL_REQUEST("views/DeliveryRequest.fxml"),
  FURNITURE_REQUEST("views/DeliveryRequest.fxml"),
  FLOWER_REQUEST("views/DeliveryRequest.fxml"),
  MAP("views/Map.fxml"),
  LOGIN("views/Login.fxml"),
  ADMINHOME("views/AdminHome.fxml"),
  MAP_EDITOR("views/MapEditor.fxml"),
  SORT_ORDERS("views/SortOrders.fxml"),
  SCREENSAVER("views/Screensaver.fxml");

  private final String filename;

  Screen(String filename) {
    this.filename = filename;
  }

  public String getFilename() {
    return filename;
  }
}
