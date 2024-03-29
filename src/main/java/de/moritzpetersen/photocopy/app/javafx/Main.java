package de.moritzpetersen.photocopy.app.javafx;

import static de.moritzpetersen.photocopy.util.LambdaUtils.*;

import atlantafx.base.theme.NordDark;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main extends Application {
  public static void main(String[] args) {
    Application.launch(Main.class, args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    Application.setUserAgentStylesheet(new NordDark().getUserAgentStylesheet());

    FileListView fileListView = new FileListView();
    ConfigView configView = new ConfigView();

    Button importButton = new Button("Run PhotoCopy");
    ProgressBar importProgress = new ProgressBar(0);

    HBox contentBox;
    HBox importBox;

    Scene scene =
        new Scene(
            new VBox(
                contentBox =
                    new HBox(
                        fileListView, new VBox(configView, importBox = new HBox(importButton))),
                importProgress));

    VBox.setVgrow(contentBox, Priority.ALWAYS);
    VBox.setVgrow(importBox, Priority.ALWAYS);
    HBox.setHgrow(fileListView, Priority.ALWAYS);
    HBox.setMargin(importButton, new Insets(20));
    importBox.setAlignment(Pos.BOTTOM_RIGHT);
    importProgress.setMaxWidth(Double.MAX_VALUE);

    stage.setTitle("PhotoCopy");
    stage.setScene(scene);
    stage.show();
  }
}
