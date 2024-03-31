package de.moritzpetersen.photocopy.app.swing;

import static de.moritzpetersen.photocopy.util.LambdaUtils.*;

import com.drew.imaging.ImageProcessingException;
import de.moritzpetersen.factory.Factory;
import de.moritzpetersen.photocopy.app.javafx.DragAndDrop;
import de.moritzpetersen.photocopy.config.Config;
import de.moritzpetersen.photocopy.copy.CopyLog;
import de.moritzpetersen.photocopy.metadata.PhotoMetadata;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import lombok.Getter;

public class FileList extends JList<FileList.FileListItem> {
  private final Config config;
  @Getter private Path basePath;

  public FileList() {
    super(new DefaultListModel<>());
    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    setSelectionModel(new NoSelectionModel());
    setCellRenderer(new Renderer());

    config = Factory.getInstance(Config.class);

    DragAndDrop.enableDrop(this, this::setBasePath);
  }

  public void setBasePath(Path basePath) {
    this.basePath = basePath;

    CopyLog copyLog = config.isAvoidDuplicates() ? new CopyLog(basePath) : null;

    DefaultListModel<FileListItem> model = (DefaultListModel<FileListItem>) getModel();
    updateSwing(model::removeAllElements);

    runAsync(
        () ->
            sneaky(() -> Files.walk(basePath))
                .parallel()
                .filter(Files::isRegularFile)
                .filter(file -> copyLog == null || !copyLog.exists(file))
                .map(FileListItem::new)
                .filter(FileListItem::isValid)
                .forEach(updateSwing(model::addElement)));
  }

  public static class FileListItem {
    private final Path path;
    @Getter private final boolean isValid;

    public FileListItem(Path path) {
      this.path = path;
      boolean isValid;
      try {
        new PhotoMetadata(path);
        isValid = true;
      } catch (IOException | ImageProcessingException e) {
        isValid = false;
      }
      this.isValid = isValid;
    }

    @Override
    public String toString() {
      return path.getFileName().toString();
    }
  }

  private static class Renderer extends DefaultListCellRenderer {

    public static final Color ALT_ROW_COLOR = UIManager.getColor("PhotoCopy.list.alternateRow");

    @Override
    public Component getListCellRendererComponent(
        JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      JComponent component =
          (JComponent)
              super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      if (index % 2 == 0) {
        component.setBackground(ALT_ROW_COLOR);
      }
      component.setBorder(new EmptyBorder(6, 4, 6, 4));
      return component;
    }
  }

  private static class NoSelectionModel extends DefaultListSelectionModel {
    @Override
    public void setAnchorSelectionIndex(int anchorIndex) {}

    @Override
    public void setLeadAnchorNotificationEnabled(boolean flag) {}

    @Override
    public void setLeadSelectionIndex(int leadIndex) {}

    @Override
    public void setSelectionInterval(int index0, int index1) {}
  }
}
