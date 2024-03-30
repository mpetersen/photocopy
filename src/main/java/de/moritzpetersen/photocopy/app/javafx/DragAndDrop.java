package de.moritzpetersen.photocopy.app.javafx;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class DragAndDrop {
  public static void enableDrop(Node node, Consumer<Path> dropConsumer) {
    node.setOnDragOver(handle((event, dir) -> event.acceptTransferModes(TransferMode.COPY_OR_MOVE)));
    node.setOnDragDropped(handle((event, dir) -> dropConsumer.accept(dir)));
  }

  private static EventHandler<? super DragEvent> handle(
      BiConsumer<DragEvent, Path> consumer) {
    return event -> {
      Path dir = getDroppedDirectory(event);
      if (dir != null) {
        consumer.accept(event, dir);
      }
      event.consume();
    };
  }

  private static Path getDroppedDirectory(DragEvent event) {
    Dragboard dragboard = event.getDragboard();
    if (dragboard.hasFiles()) {
      List<File> files = dragboard.getFiles();
      if (files.size() == 1) {
        File file = files.getFirst();
        if (file.isDirectory()) {
          return file.toPath();
        }
      }
    }
    return null;
  }
}
