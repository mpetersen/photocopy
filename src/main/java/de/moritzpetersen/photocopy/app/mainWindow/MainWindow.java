package de.moritzpetersen.photocopy.app.mainWindow;

import static de.moritzpetersen.photocopy.util.LambdaUtils.runAsync;
import static de.moritzpetersen.photocopy.util.LambdaUtils.updateSwing;

import com.formdev.flatlaf.FlatDarculaLaf;
import de.moritzpetersen.factory.Factory;
import de.moritzpetersen.photocopy.app.fileList.FileList;
import de.moritzpetersen.photocopy.app.fileList.FileListItem;
import de.moritzpetersen.photocopy.app.knownLocations.KnownLocationsDialog;
import de.moritzpetersen.photocopy.app.layout.Grid;
import de.moritzpetersen.photocopy.app.textComponent.FilePicker;
import de.moritzpetersen.photocopy.app.textComponent.JTextFieldComponent;
import de.moritzpetersen.photocopy.app.textComponent.TextComponent;
import de.moritzpetersen.photocopy.config.Config;
import de.moritzpetersen.util.swing.DragAndDrop;
import de.moritzpetersen.util.swing.WindowManager;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.inject.Inject;
import javax.swing.*;
import lombok.Getter;

public class MainWindow extends JFrame {
  private static final String TITLE = "Photocopy";
  private SourceLabel sourceLabel;
  private FileList fileList;
  private JButton runButton;
  private Grid grid;
  @Inject private Config config;
  @Getter private Path basePath;

  /** Show preview */
  public static void main(String[] args) {
    FlatDarculaLaf.setup();
    Factory.create(MainWindow.class).init().setVisible(true);
  }

  public MainWindow init() {
    setTitle(TITLE);
    setDefaultCloseOperation(EXIT_ON_CLOSE);

    sourceLabel = new SourceLabel("Drop here to import:");
    fileList = new FileList();

    runButton = new JButton("Run " + TITLE);

    grid = new Grid(this).withColumns(3).withPadding(20).withHGaps(20, 8).withVGap(14);
    grid.add(sourceLabel);
    addTextComponent(
        "Rename on import:",
        new JTextFieldComponent(20),
        config.getFormatStr(),
        config::setFormatStr,
        null);
    grid.spanRows().fill().resize().add(fileList);
    addTextComponent(
        "Target:",
        new FilePicker(20, 2),
        config.getTarget(),
        str -> config.setTarget(Path.of(str)),
        Path::toAbsolutePath);
    addCheckBox("Erase target before import", config.isEraseEnabled(), config::setEraseEnabled);
    addCheckBox("Open target after import", config.isOpenAfterCopy(), config::setOpenAfterCopy);
    addCheckBox("Avoid duplicates", config.isAvoidDuplicates(), config::setAvoidDuplicates);
    addCheckBox("Eject after import", config.isEjectEnabled(), config::setEjectEnabled);
    addCheckBox("Auto-import on drop", config.isImportOnDrop(), config::setImportOnDrop);
    addCheckBox(
        "Auto-import known locations",
        config.isImportKnownLocations(),
        config::setImportKnownLocations);
    JButton clearButton = new JButton("Clear known locations");
    grid.spanColumns().add(clearButton);
    clearButton.addActionListener(event -> new KnownLocationsDialog(MainWindow.this).setVisible(true));
    addCheckBox("Quit after import", config.isQuitAfterImport(), config::setQuitAfterImport);
    grid.spanColumns().spanRows().resizeY().lastLineEnd().add(runButton);

    pack();
    WindowManager.apply(this);
    return this;
  }

  private void addButton(String label, Runnable actionHandler) {
    JButton component = new JButton(label);
    grid.spanColumns().add(component);

    component.addActionListener(
        event -> {
          actionHandler.run();
          config.save();
        });
  }

  private void addCheckBox(String label, boolean initialValue, Consumer<Boolean> changeHandler) {
    JCheckBox component = new JCheckBox(label, initialValue);
    grid.spanColumns().add(component);

    component.addActionListener(
        event -> {
          updateConfig(changeHandler, component.isSelected());
        });
  }

  private void addTextComponent(
      String label,
      TextComponent component,
      Object initialValue,
      Consumer<String> changeHandler,
      Function<Path, ?> dropFunction) {
    component.setText(Objects.toString(initialValue, ""));
    grid.add(label).fillX().add((JComponent) component);

    component.addKeyListener(
        new KeyAdapter() {
          @Override
          public void keyTyped(KeyEvent e) {
            updateConfig(changeHandler, component.getText());
          }
        });

    if (dropFunction != null) {
      DragAndDrop.enableDrop(
          (JComponent) component,
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

  public void setBasePath(Path basePath) {
    this.basePath = basePath;
    sourceLabel.setText(basePath.toAbsolutePath() + ":");
  }

  public void setOnAction(Runnable handler) {
    runButton.addActionListener(event -> runAsync(handler::run));
  }

  public JComponent getDropComponent() {
    return fileList;
  }

  public void addFile(Path path) {
    updateSwing(() -> fileList.getModel().addElement(new FileListItem(path)));
  }

  public void removeAllFiles() {
    updateSwing(() -> fileList.getModel().removeAllElements());
  }

  public void disableActionButton() {
    runButton.setEnabled(false);
  }

  public void enableActionButton() {
    runButton.setEnabled(true);
  }
}
