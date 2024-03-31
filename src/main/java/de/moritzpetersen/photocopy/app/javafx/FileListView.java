package de.moritzpetersen.photocopy.app.javafx;

import static de.moritzpetersen.photocopy.util.LambdaUtils.*;
import static de.moritzpetersen.photocopy.util.LambdaUtils.updateJavaFX;

import de.moritzpetersen.factory.Factory;
import de.moritzpetersen.photocopy.app.javafx.model.FileObject;
import de.moritzpetersen.photocopy.config.Config;
import de.moritzpetersen.photocopy.util.LambdaUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ListView;
import lombok.Getter;

@Getter
public class FileListView extends ListView<FileObject> {
  private Path sourceDir;
  private EventHandler<ActionEvent> eventHandler;

  public FileListView() {
    DragAndDrop.enableDrop(
        this,
        droppedDir -> {
          Config config = Factory.inject(Config.class);

          this.sourceDir = droppedDir;

          ObservableList<FileObject> items = getItems();
          updateJavaFX(items::clear);

          runAsync(
              () -> {
                try (Stream<Path> files = Files.walk(droppedDir)) {
                  files
                      .filter(Files::isRegularFile)
                      .map(FileObject::new)
                      .filter(FileObject::isValid)
                      .forEach(LambdaUtils.updateJavaFX(items::add));
                } catch (IOException e) {
                  throw new RuntimeException(e);
                }
              });

          if (config.isImportOnDrop()) {
            runAsync(
                () -> {
                  eventHandler.handle(new ActionEvent());
                });
          }
        });
  }

  public void setOnAction(EventHandler<ActionEvent> eventHandler) {
    this.eventHandler = eventHandler;
  }
}
