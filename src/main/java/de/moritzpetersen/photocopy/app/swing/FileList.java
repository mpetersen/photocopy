package de.moritzpetersen.photocopy.app.swing;

import static de.moritzpetersen.photocopy.util.LambdaUtils.sneaky;

import de.moritzpetersen.photocopy.app.javafx.DragAndDrop;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class FileList extends JList<FileList.FileListItem> {
  public FileList() {
    super(new DefaultListModel<>());
    setCellRenderer(new Renderer());

    DefaultListModel<FileListItem> model = (DefaultListModel<FileListItem>) getModel();

    DragAndDrop.enableDrop(
        this,
        sneaky(
            dir -> {
              model.removeAllElements();
              Files.walk(dir).map(FileListItem::new).forEach(model::addElement);
            }));
  }

  public static class FileListItem {
    private final Path path;

    public FileListItem(Path path) {
      this.path = path;
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
}
