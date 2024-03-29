package de.moritzpetersen.photocopy.app.javafx;

import static de.moritzpetersen.photocopy.util.LambdaUtils.*;
import static de.moritzpetersen.photocopy.util.LambdaUtils.runLater;

import de.moritzpetersen.photocopy.app.javafx.model.FileObject;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.Dragboard;

public class FileListView extends TableView<FileObject> {
  public FileListView() {
    TableColumn<FileObject, String> nameColumn = new TableColumn<>("Name");
    nameColumn.setCellValueFactory(param -> param.getValue().nameProperty());
    TableColumn<FileObject, Boolean> validColumn = new TableColumn<>("Valid");
    validColumn.setCellValueFactory(param -> param.getValue().validProperty());
    validColumn.setCellFactory(column -> new CheckBoxTableCell<>());
    TableColumn<FileObject, Boolean> importedColumn = new TableColumn<>("Imported");
    importedColumn.setCellValueFactory(param -> param.getValue().importedProperty());
    importedColumn.setCellFactory(column -> new CheckBoxTableCell<>());
    getColumns().addAll(nameColumn, validColumn, importedColumn);

    setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

    DragAndDrop.enableDrop(this,
        event -> {
          ObservableList<FileObject> items = getItems();
          runLater(items::clear);
          Dragboard dragboard = event.getDragboard();
          List<File> files = dragboard.getFiles();

          runAsync(
              () ->
                  files.stream()
                      .map(File::toPath)
                      .flatMap(sneaky(Files::walk))
                      .filter(Files::isRegularFile)
                      .map(FileObject::new)
                      .forEach(runLater(items::add)));

          event.consume();
        });
  }
}
