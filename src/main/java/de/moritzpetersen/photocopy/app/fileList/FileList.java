package de.moritzpetersen.photocopy.app.fileList;

import java.awt.*;
import java.nio.file.Path;
import javax.swing.*;

public class FileList extends JPanel {
  private Path basePath;

  public FileList() {
    JList<FileListItem> list = new JList<>();
    list.setCellRenderer(new AlternatingRowCellRenderer());
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    list.setSelectionModel(new NoSelectionModel());

    JScrollPane scrollPane = new JScrollPane(
        list,
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    setLayout(new BorderLayout());
    add(
        scrollPane,
        BorderLayout.CENTER);
  }

  public void setBasePath(Path basePath) {
    this.basePath = basePath;
  }
}
