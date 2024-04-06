package de.moritzpetersen.photocopy.util;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class FileExtensionTest {
  @Test
  void testSupported() {
    assertSupported("/hello/world/DSC_123.NEF", true);
  }

  @Test
  void testNotSupported() {
    assertSupported("/hello/world/some.txt", false);
    assertSupported("/hello/world/some", false);
    assertSupported("/hello/world/.jpg", false);
  }

  private static void assertSupported(String pathStr, boolean expected) {
    Path path = Path.of(pathStr);
    assertEquals(expected, FileExtension.isSupported(path), "Path: " + pathStr);
  }
}
