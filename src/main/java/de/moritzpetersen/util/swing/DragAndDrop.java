package de.moritzpetersen.util.swing;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.*;

public class DragAndDrop {
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
