package de.moritzpetersen.photocopy.app.javafx;


import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;

public class DragAndDrop {
  public static void enableDrop(Node node, EventHandler<? super DragEvent> handler){
    node.setOnDragOver(
        event -> {
          if (event.getGestureSource() != node && event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
          }
          event.consume();
        });

    node.setOnDragDropped(handler);
  }
}
