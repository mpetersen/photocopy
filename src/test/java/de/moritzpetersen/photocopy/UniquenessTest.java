package de.moritzpetersen.photocopy;

import de.moritzpetersen.photocopy.copy.SizeComparator;
import de.moritzpetersen.photocopy.copy.Uniqueness;
import de.moritzpetersen.photocopy.version.VersionedPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UniquenessTest {
  private Uniqueness uniqueness;

  @BeforeEach
  void reset() throws IOException {
    Files.list(base())
        .forEach(
            path -> {
              try {
                Files.deleteIfExists(path);
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            });
    uniqueness = new Uniqueness();
  }

  @Test
  void verifyFileDoesNotExist() {
    VersionedPath a = path("a.txt");
    Path path = uniqueness.toPath(a);
    assertEqualsPath("a.txt", path);
  }

  @Test
  void verifyFileExists() throws IOException {
    VersionedPath a = create("a.txt", "Hello, World!");
    Path path = uniqueness.toPath(a);
    assertEqualsPath("a-2.txt", path);
  }

  @Test
  void verifyFileDoesNotExistButConcurrentCreate() throws IOException {
    VersionedPath a1 = path("a.txt");
    uniqueness.toPath(a1);

    VersionedPath a2 = path("a.txt");
    Path path = uniqueness.toPath(a2);
    assertEqualsPath("a-2.txt", path);
  }

  @Test
  void verifySameFile() throws Exception {
    VersionedPath a1 = create("a.txt", "Hello, World!");
    Path path = uniqueness.toPath(a1, new SizeComparator(a1.toPath()));
    assertNull(path);
  }

  private void assertEqualsPath(String pathStr, Path path) {
    assertEquals(path(pathStr).toPath(), path);
  }

  private VersionedPath create(String pathStr, String content) throws IOException {
    VersionedPath versionedPath = path(pathStr);
    Path path = versionedPath.toPath();
    Files.createDirectories(path.getParent());
    Files.writeString(path, content);
    return versionedPath;
  }

  private VersionedPath path(String pathStr) {
    return new VersionedPath(base().resolve(pathStr));
  }

  private Path base() {
    return Path.of("target/test-output/" + getClass().getName());
  }
}