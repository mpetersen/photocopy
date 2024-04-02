package de.moritzpetersen.photocopy.app;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javax.swing.*;

public class DragAndDrop {
  public static void enableDrop(Node node, Consumer<Path> dropConsumer) {
    node.setOnDragOver(
        handle((event, dir) -> event.acceptTransferModes(TransferMode.COPY_OR_MOVE)));
    node.setOnDragDropped(handle((event, dir) -> dropConsumer.accept(dir)));
  }

  private static EventHandler<? super DragEvent> handle(BiConsumer<DragEvent, Path> consumer) {
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

  public static void enableDrop(JComponent comp, Consumer<Path> dropConsumer) {
    comp.setTransferHandler(
        new TransferHandler() {
          @Override
          public boolean canImport(TransferSupport support) {
            return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
          }

          @Override
          public boolean importData(TransferSupport support) {
            Path dir = getDroppedDirectory(support);
            if (dir != null) {
              dropConsumer.accept(dir);
              return true;
            }
            return false;
          }

          private Path getDroppedDirectory(TransferSupport support) {
            for (DataFlavor dataFlavor : support.getDataFlavors()) {
              if (dataFlavor.isFlavorJavaFileListType()) {
                try {
                  //noinspection unchecked
                  List<File> files =
                      (List<File>)
                          support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                  if (files.size() == 1) {
                    File file = files.getFirst();
                    if (file.isDirectory()) {
                      return file.toPath();
                    }
                  }
                } catch (UnsupportedFlavorException | IOException e) {
                  // ignore
                }
              }
            }
            return null;
          }
        });
  }
}
