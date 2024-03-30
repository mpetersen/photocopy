package de.moritzpetersen.photocopy.app.javafx;

import static de.moritzpetersen.photocopy.util.LambdaUtils.*;

import atlantafx.base.theme.NordDark;
import de.moritzpetersen.factory.Factory;
import de.moritzpetersen.photocopy.config.Config;
import de.moritzpetersen.photocopy.copy.CopyProcessor;
import de.moritzpetersen.photocopy.copy.CopyStats;
import de.moritzpetersen.photocopy.volume.Volume;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

    EventHandler<ActionEvent> runPhotoCopy = event -> {
      Config config = Factory.inject(Config.class);
      CopyProcessor copyProcessor = Factory.inject(CopyProcessor.class);

      CopyStats stats =
          new CopyStats() {
            private int counter = 0;

            @Override
            public void addStats(long bytesCopied) {
              super.addStats(bytesCopied);
              counter++;
              double progress = (double) counter / fileListView.getItems().size();
              runLater(() -> importProgress.setProgress(progress));
            }
          };

      Path sourceDir = fileListView.getSourceDir();
      Volume sourceVolume = Volume.of(sourceDir);

      if (!config.getKnownLocations().contains(sourceDir)) {
        config.getKnownLocations().add(sourceDir);
        config.save();
      }

      try {
        if (sourceVolume != null) {
          sourceVolume.addEjectFailedListener(
              b -> {
                System.out.println("Eject failed: " + sourceVolume);
              });
          copyProcessor.doCopy(sourceVolume, config, stats);
        } else {
          copyProcessor.doCopy(sourceDir, config, stats);
        }
        if (config.isQuitAfterImport()) {
          System.exit(0);
        }
      } catch (IOException | ExecutionException | InterruptedException e) {
        throw new RuntimeException(e);
      }
    };

    importButton.setOnAction(runPhotoCopy);
    fileListView.setOnAction(runPhotoCopy);

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
