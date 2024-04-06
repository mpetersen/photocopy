package de.moritzpetersen.photocopy.app.fileList;

import java.nio.file.Path;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FileListItem {
  private Path path;

  @Override
  public String toString() {
    return path.getFileName().toString();
  }
}
