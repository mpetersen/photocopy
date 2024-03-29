package de.moritzpetersen.photocopy.app.javafx.model;

import com.drew.imaging.ImageProcessingException;
import de.moritzpetersen.photocopy.metadata.PhotoMetadata;
import java.io.IOException;
import java.nio.file.Path;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FileObject {
  private StringProperty name;
  private BooleanProperty valid;
  private BooleanProperty imported;

  public FileObject(Path path) {
    name = new SimpleStringProperty(this, "name", path.getFileName().toString());
    try {
      PhotoMetadata metadata = new PhotoMetadata(path);
      valid = new SimpleBooleanProperty(this, "valid", true);
    } catch (IOException | ImageProcessingException | NullPointerException e) {
      valid = new SimpleBooleanProperty(this, "valid", false);
    }
    imported = new SimpleBooleanProperty(false);
  }

  public StringProperty nameProperty() {
    return name;
  }

  public BooleanProperty validProperty() {
    return valid;
  }

  public BooleanProperty importedProperty() {
    return imported;
  }
}
