package de.moritzpetersen.photocopy;

import java.nio.file.Path;

public interface FileComparator {
  boolean isSame(Path path);
}
