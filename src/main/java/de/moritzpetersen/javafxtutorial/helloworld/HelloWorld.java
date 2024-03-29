package de.moritzpetersen.javafxtutorial.helloworld;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class HelloWorld extends Application {
  @Override
  public void start(Stage primaryStage) throws Exception {
    Button button = new Button("Say 'Hello World'");
    button.setOnAction(
        e -> {
          Alert alert = new Alert(Alert.AlertType.INFORMATION, "Hello World!?");
          alert.showAndWait();
        });

    StackPane root = new StackPane();
    root.getChildren().add(button);

    Scene scene = new Scene(root, 500, 300);

    primaryStage.setScene(scene);

    primaryStage.show();
  }

  public static void main(String[] args) {
    Application.launch(HelloWorld.class, args);
  }
}
