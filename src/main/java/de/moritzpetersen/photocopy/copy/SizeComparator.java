package de.moritzpetersen.photocopy.copy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SizeComparator implements FileComparator {
  private final long otherSize;

  public SizeComparator(Path path) throws IOException {
    otherSize = Files.size(path);
  }

  @Override
  public boolean isSame(Path path) {
    try {
      return Files.size(path) == otherSize;
    } catch (IOException e) {
      return false;
    }
  }
}
