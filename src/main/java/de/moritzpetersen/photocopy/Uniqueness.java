package de.moritzpetersen.photocopy;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class Uniqueness {
  private VersionedPath versionedPath;

  private final Set<Path> pathInstances = new HashSet<>();

  public Path toPath(VersionedPath versionedPath, FileComparator... fileComparators) {
    synchronized (pathInstances) {
      Path path = versionedPath.toPath();

      while (pathInstances.contains(path) || Files.exists(path)) {
        if (isSame(path, fileComparators)) {
          return null;
        }
        versionedPath.incrementVersion();
        path = versionedPath.toPath();
      }
      pathInstances.add(path);
      return path;
    }
  }

  private boolean isSame(Path path, FileComparator[] fileComparators) {
    if (fileComparators == null || fileComparators.length == 0) {
      return false;
    }
    for (FileComparator fileComparator : fileComparators) {
      if (!fileComparator.isSame(path)) {
        return false;
      }
    }
    return true;
  }
}
