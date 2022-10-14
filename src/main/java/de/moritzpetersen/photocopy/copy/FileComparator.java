package de.moritzpetersen.photocopy.copy;

import java.nio.file.Path;

public interface FileComparator {
  boolean isSame(Path path);
}
