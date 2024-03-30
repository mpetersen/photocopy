package de.moritzpetersen.photocopy.app.javafx;

import de.moritzpetersen.factory.Factory;
import de.moritzpetersen.photocopy.config.Config;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class ConfigView extends GridPane {
  private int rowIndex = 0;

  public ConfigView() {
    Config config = Factory.inject(Config.class);

    setPadding(new Insets(20));
    setHgap(8);
    setVgap(14);

    addTextField("Rename on copy:", config::getFormatStr, config::setFormatStr);
    TextField target =
        addTextField("Target:", config::getTarget, str -> config.setTarget(Path.of(str)));
    DragAndDrop.enableDrop(target, file -> target.setText(file.toAbsolutePath().toString()));
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

    addEventHandler(ConfigUpdateEvent.CONFIG, event -> config.save());
  }

  private void addButton(String label, Runnable actionHandler) {
    Button node = new Button(label);

    node.setOnAction(
        event -> {
          actionHandler.run();
        });

    addDoubleColumnSpan(node);
  }

  private void addRadioButton(
      String label, Supplier<Boolean> valueProvider, Consumer<Boolean> updateHandler) {
    RadioButton node = new RadioButton(label);

    node.selectedProperty().set(valueProvider.get());
    node.setOnAction(
        event -> {
          updateHandler.accept(node.selectedProperty().get());
          fireEvent(new ConfigUpdateEvent());
        });

    addDoubleColumnSpan(node);
  }

  private void addDoubleColumnSpan(Node node) {
    add(node, 0, rowIndex);
    rowIndex++;

    GridPane.setColumnSpan(node, 2);
  }

  private TextField addTextField(
      String label,
      Supplier<Object> valueProvider,
      Consumer<String> updateHandler) {
    TextField node = new TextField(Objects.toString(valueProvider.get(), ""));
    node.setPrefWidth(300);

    node.setOnKeyTyped(
        event -> {
          updateHandler.accept(node.getText());
          fireEvent(new ConfigUpdateEvent());
        });

    add(new Label(label), 0, rowIndex);
    add(node, 1, rowIndex);
    rowIndex++;

    return node;
  }

  private static class ConfigUpdateEvent extends Event {
    private static EventType<ConfigUpdateEvent> CONFIG = new EventType<>(EventType.ROOT, "CONFIG");

    public ConfigUpdateEvent() {
      super(CONFIG);
    }
  }
}
