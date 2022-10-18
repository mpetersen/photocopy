package de.moritzpetersen.photocopy.version;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VersionedPathTest {
  @Test
  void ensureNoSuffix() {
    String pathStr = "/a/b/c/file";
    Path path = Path.of(pathStr);
    VersionedPath versionedPath = new VersionedPath(path);
    assertEquals(pathStr, versionedPath.toPath().toString());

    versionedPath.incrementVersion();
    assertEquals(pathStr + "-2", versionedPath.toPath().toString());
  }

  @Test
  void ensureSuffix() {
    String pathStr = "/a/b/c/file.small.jpg";
    Path path = Path.of(pathStr);
    VersionedPath versionedPath = new VersionedPath(path);
    assertEquals(pathStr, versionedPath.toPath().toString());

    versionedPath.incrementVersion();
    assertEquals(pathStr.replace(".jpg", "-2.jpg"), versionedPath.toPath().toString());
  }
}