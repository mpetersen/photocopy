package de.moritzpetersen.testing;

import de.moritzpetersen.photocopy.util.DeleteFileVisitor;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractFileTest {
  @BeforeEach
  void cleanUp() throws IOException {
    Path base = path("");
    if (Files.exists(base)) {
      Files.walkFileTree(base, new DeleteFileVisitor());
    }
  }

  protected Path path(String pathStr) {
    return Path.of("target", "test-output", getClass().getName(), pathStr);
  }

  protected Path create(String name, String content) throws IOException {
    Path path = path(name);
    Files.createDirectories(path.getParent());
    Files.writeString(path, content);
    return path;
  }

  protected void asssertExists(boolean expected, Path path) {
    assertEquals(expected, Files.exists(path), path.toString());
  }
}
