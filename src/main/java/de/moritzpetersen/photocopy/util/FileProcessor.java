package de.moritzpetersen.photocopy.util;

import java.nio.file.Path;

public interface FileProcessor {
  abstract class Default implements FileProcessor {
    @Override
    public void setup(Path path) {
    }

    @Override
    public void shutdown(Path path) {
    }
  }

  void setup(Path path);

  void shutdown(Path path);

  void process(Path path);
}
