package de.moritzpetersen.photocopy.app.fileList;

import java.awt.*;
import javax.swing.*;
import lombok.Getter;

public class FileList extends JPanel {
  @Getter private final DefaultListModel<FileListItem> model = new DefaultListModel<>();

  public FileList() {
    JList<FileListItem> list = new JList<>(model);
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
}
