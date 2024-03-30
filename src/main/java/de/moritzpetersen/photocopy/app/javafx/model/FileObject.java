package de.moritzpetersen.photocopy.app.javafx.model;

import com.drew.imaging.ImageProcessingException;
import de.moritzpetersen.photocopy.metadata.PhotoMetadata;
import java.io.IOException;
import java.nio.file.Path;

public class FileObject {
  private PhotoMetadata metadata;
  private String name;

  public FileObject(Path path) {
    name = path.getFileName().toString();
    try {
      metadata = new PhotoMetadata(path);
    } catch (IOException | ImageProcessingException | NullPointerException e) {
      // ignore;
    }
  }

  public boolean isValid() {
    return metadata != null;
  }

  @Override
  public String toString() {
    return name;
  }
}
