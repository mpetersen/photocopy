package de.moritzpetersen.photocopy.app;

import com.formdev.flatlaf.FlatDarculaLaf;
import de.moritzpetersen.factory.Factory;
import de.moritzpetersen.photocopy.app.fileList.FileList;
import de.moritzpetersen.photocopy.config.Config;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.swing.*;

public class MainWindow extends JFrame {
  private static final String TITLE = "PhotoCopy";
  private final SourceLabel sourceLabel;
  private final FileList fileList;
  private final JProgressBar importProgress;
  private final JButton runButton;
  private final Grid grid;
  private final Config config;

  public MainWindow() throws HeadlessException {
    super(TITLE);
    setDefaultCloseOperation(EXIT_ON_CLOSE);

    sourceLabel = new SourceLabel("Drop here to import:");
    fileList = new FileList();
    DragAndDrop.enableDrop(fileList, path -> {
      sourceLabel.setText(path.toAbsolutePath() + ":");
      fileList.setBasePath(path);
    });

    runButton = new JButton("Run " + TITLE);
    importProgress = new JProgressBar();

    config = Factory.getInstance(Config.class);

    grid = new Grid(this)
        .withColumns(3)
        .withPadding(20)
        .withHGap(8)
        .withVGap(14);
    grid.add(sourceLabel);
    addTextField("Rename on copy:", config.getFormatStr(), config::setFormatStr);
    grid.spanRows().fill().resize().add(fileList);
    addTextField("Target:", config.getTarget(), str -> config.setTarget(Path.of(str)), Path::toAbsolutePath);
    addRadioButton("Erase target before copy", config.isEraseEnabled(), config::setEraseEnabled);
    addRadioButton("Open target after copy", config.isOpenAfterCopy(), config::setOpenAfterCopy);
    addRadioButton("Avoid duplicates", config.isAvoidDuplicates(), config::setAvoidDuplicates);
    addRadioButton("Eject after copy", config.isEjectEnabled(), config::setEjectEnabled);
    addRadioButton("Auto-import on drop", config.isImportOnDrop(), config::setImportOnDrop);
    addRadioButton("Auto-import known locations", config.isImportKnownLocations(), config::setImportKnownLocations);
    addButton("Clear known locations", () -> config.getKnownLocations().clear());
    addRadioButton("Quit after import", config.isQuitAfterImport(), config::setQuitAfterImport);
    grid.spanColumns().resizeY().lastLineEnd().add(runButton);
    grid.spanColumns().spanRows().fillX().add(importProgress);

    pack();
    setVisible(true);
  }

  private void addButton(String label, Runnable actionHandler) {
    JButton component = new JButton(label);
    grid.spanColumns().add(component);

    component.addActionListener(event -> {
      actionHandler.run();
      config.save();
    });
  }

  private void addRadioButton(String label, boolean initialValue, Consumer<Boolean> changeHandler) {
    JRadioButton component = new JRadioButton(label, initialValue);
    grid.spanColumns().add(component);

    component.addActionListener(event -> {
      updateConfig(changeHandler, component.isSelected());
    });
  }

  private void addTextField(String label, Object initialValue, Consumer<String> changeHandler) {
    addTextField(label, initialValue, changeHandler, null);
  }

  private void addTextField(String label, Object initialValue, Consumer<String> changeHandler, Function<Path, ?> dropFunction) {
    JTextField component = new JTextField(Objects.toString(initialValue, ""), 20);
    grid.add(label).add(component);

    component.addKeyListener(new KeyAdapter() {
      @Override
      public void keyTyped(KeyEvent e) {
        updateConfig(changeHandler, component.getText());
      }
    });

    if (dropFunction != null) {
      DragAndDrop.enableDrop(
          component,
          path -> {
            String value = Objects.toString(dropFunction.apply(path), "");
            component.setText(value);
            updateConfig(changeHandler, value);
          });
    }
  }

  private <T> void updateConfig(Consumer<T> changeHandler, T value) {
    changeHandler.accept(value);
    config.save();
  }

  /**
   * Show preview
   */
  public static void main(String[] args){
    FlatDarculaLaf.setup();
    new MainWindow();
  }
}
