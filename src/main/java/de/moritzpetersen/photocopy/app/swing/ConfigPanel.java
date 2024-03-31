package de.moritzpetersen.photocopy.app.swing;

import de.moritzpetersen.factory.Factory;
import de.moritzpetersen.photocopy.app.javafx.DragAndDrop;
import de.moritzpetersen.photocopy.config.Config;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.swing.*;

public class ConfigPanel extends JPanel {
  private final Runnable configHandler;
  private static final Grid GRID = new Grid(2, 10, 20, 8, 14);
  private static final Grid.Constraints FIRST_COLUMN = GRID.getConstraints(0, 1);
  private static final Grid.Constraints SECOND_COLUMN = GRID.getConstraints(1, 1);
  private static final Grid.Constraints FIRST_AND_SECOND_COLUMN = GRID.getConstraints(0, 2);
  private int rowIndex = 0;

  public ConfigPanel() {
    super(new GridBagLayout());

    Config config = Factory.getInstance(Config.class);
    configHandler = config::save;

    addTextField("Rename on copy:", config::getFormatStr, config::setFormatStr);
    JTextField target =
        addTextField("Target:", config::getTarget, str -> config.setTarget(Path.of(str)));
    DragAndDrop.enableDrop(
        target,
        file -> {
          target.setText(file.toAbsolutePath().toString());
          configHandler.run();
        });
    addRadioButton("Erase target before copy", config::isEraseEnabled, config::setEjectEnabled);
    addRadioButton("Open target after copy", config::isOpenAfterCopy, config::setOpenAfterCopy);
    addRadioButton("Avoid duplicates", config::isAvoidDuplicates, config::setAvoidDuplicates);
    addRadioButton("Eject after copy", config::isEjectEnabled, config::setEjectEnabled);
    addRadioButton("Auto-import on drop", config::isImportOnDrop, config::setImportOnDrop);
    addRadioButton(
        "Auto-import known locations",
        config::isImportKnownLocations,
        config::setImportKnownLocations);
    addButton("Clear known locations", () -> config.setKnownLocations(null));
    addRadioButton("Quit after import", config::isQuitAfterImport, config::setQuitAfterImport);
  }

  private void addButton(String label, Runnable actionHandler) {
    JButton button = new JButton(label);
    //    button.putClientProperty("JButton.buttonType", "roundRect");

    button.addActionListener(
        event -> {
          actionHandler.run();
          configHandler.run();
        });

    add(button, FIRST_AND_SECOND_COLUMN.atRow(rowIndex++));
  }

  private void addRadioButton(
      String label, Supplier<Boolean> valueProvider, Consumer<Boolean> updateHandler) {
    JRadioButton radioButton = new JRadioButton(label, valueProvider.get());

    radioButton.addActionListener(
        event -> {
          updateHandler.accept(radioButton.isSelected());
          configHandler.run();
        });

    add(radioButton, FIRST_AND_SECOND_COLUMN.atRow(rowIndex++));
  }

  private JTextField addTextField(
      String label, Supplier<Object> valueProvider, Consumer<String> updateHandler) {
    JTextField textField = new JTextField(Objects.toString(valueProvider.get(), ""), 20);

    // TODO check if ActionListener also works
    textField.addKeyListener(
        new KeyAdapter() {
          @Override
          public void keyTyped(KeyEvent e) {
            updateHandler.accept(textField.getText());
            configHandler.run();
          }
        });

    add(new JLabel(label), FIRST_COLUMN.atRow(rowIndex));
    add(textField, SECOND_COLUMN.atRow(rowIndex++));

    return textField;
  }
}
