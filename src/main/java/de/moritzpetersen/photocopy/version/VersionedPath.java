package de.moritzpetersen.photocopy.version;

import lombok.Setter;

import java.nio.file.Path;

public class VersionedPath {
  private final Path dir;
  private final VersioningStrategy versioningStrategy;
  @Setter private String name;
  private String suffix = "";

  public VersionedPath(Path originalPath) {
    this(originalPath, new DefaultVersioningStrategy());
  }

  public VersionedPath(Path originalPath, VersioningStrategy versioningStrategy) {
    this.versioningStrategy = versioningStrategy;
    dir = originalPath.getParent();
    String fileName = originalPath.getFileName().toString();
    int dotIndex = fileName.lastIndexOf('.');
    if (dotIndex > 0) {
      name = fileName.substring(0, dotIndex);
      suffix = fileName.substring(dotIndex);
    } else {
      name = fileName;
    }
  }

  public void incrementVersion() {
    versioningStrategy.inc();
  }

  public Path toPath() {
    return dir.resolve(toString());
  }

  public String toString() {
    return name + versioningStrategy + suffix;
  }
}
