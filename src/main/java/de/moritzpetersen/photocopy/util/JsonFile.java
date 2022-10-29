package de.moritzpetersen.photocopy.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public abstract class JsonFile {
  private static final ObjectMapper MAPPER = new ObjectMapper();
  @JsonIgnore
  private final Path path;

  protected JsonFile(Path path) {
    this.path = path;
    if (Files.exists(path)) {
      try (InputStream in = Files.newInputStream(path)) {
        MAPPER.readerForUpdating(this).readValue(in);
      } catch (IOException e) {
        log.warn("Unable to read {}: {}", path, e.getMessage());
      }
    }
  }

  public final Path getPath() {
    return path;
  }

  public void save() {
    try {
      final Path parent = path.getParent();
      if (!Files.exists(parent)) {
        Files.createDirectories(parent);
      }
      MAPPER.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), this);
    } catch (IOException e) {
      log.error("Unable to write {}: {}", path, e.getMessage());
    }
  }
}
