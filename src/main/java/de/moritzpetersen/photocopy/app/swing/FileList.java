package de.moritzpetersen.photocopy.app.swing;

import static de.moritzpetersen.factory.Factory.inject;
import static de.moritzpetersen.photocopy.util.LambdaUtils.*;

import de.moritzpetersen.photocopy.app.DragAndDrop;
import de.moritzpetersen.photocopy.app.fileList.AlternatingRowCellRenderer;
import de.moritzpetersen.photocopy.app.fileList.NoSelectionModel;
import de.moritzpetersen.photocopy.config.Config;
import de.moritzpetersen.photocopy.copy.CopyLog;
import de.moritzpetersen.photocopy.metadata.PhotoMetadata;
import de.moritzpetersen.photocopy.util.FileUtils;
import java.awt.*;
import java.nio.file.Path;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import lombok.Getter;

public class FileList extends JPanel {
  private static final EmptyBorder BORDER = new EmptyBorder(6, 4, 6, 4);
  private final Config config = inject(Config.class);
  private final JList<FileListItem> list = new JList<>(new DefaultListModel<>());
  private final JLabel title = new JLabel("Drop source directory here.");
  @Getter private Path basePath;
  private Consumer<Path> onDropConsumer;

  public FileList() {
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    list.setSelectionModel(new NoSelectionModel());
    list.setCellRenderer(new AlternatingRowCellRenderer());
    DragAndDrop.enableDrop(
        list,
        dir -> {
          setBasePath(dir);
          runAsync(() -> onDropConsumer.accept(dir));
        });

    Font font = title.getFont();
    title.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
    title.setBorder(BORDER);

    setLayout(new BorderLayout());
    add(title, BorderLayout.PAGE_START);
    add(
        new JScrollPane(
            list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER),
        BorderLayout.CENTER);
  }

  public int getItemCount() {
    return list.getModel().getSize();
  }

  public void setBasePath(Path basePath) {
    this.basePath = basePath;

    CopyLog copyLog = config.isAvoidDuplicates() ? new CopyLog(basePath) : null;

    DefaultListModel<FileListItem> model = (DefaultListModel<FileListItem>) list.getModel();
    updateSwing(
        () -> {
          title.setText(basePath.toAbsolutePath() + ":");
          model.removeAllElements();
        });

    runAsync(
        () ->
            FileUtils.safeWalk(
                basePath,
                path -> {
                  if (copyLog == null || !copyLog.exists(path)) {
                    FileListItem item = new FileListItem(path);
                    if (item.isValid()) {
                      updateSwing(() -> model.addElement(item));
                    }
                  }
                }));
  }

  public void setOnDrop(Consumer<Path> onDropConsumer) {
    this.onDropConsumer = onDropConsumer;
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
      } catch (Exception e) {
        isValid = false;
      }
      this.isValid = isValid;
    }

    @Override
    public String toString() {
      return path.getFileName().toString();
    }
  }

}
