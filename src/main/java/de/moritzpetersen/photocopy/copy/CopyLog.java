package de.moritzpetersen.photocopy.copy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.moritzpetersen.photocopy.util.JsonFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CopyLog extends JsonFile {
  private Map<String, LogEntry> entryMap;
  @JsonIgnore
  private Path base;

  public CopyLog(Path base) {
    super(base.resolve("photocopy.json"));
    this.base = base;
    if (entryMap == null) {
      entryMap = new HashMap<>();
    }
  }

  public boolean exists(Path originalPath) {
    String relativePath = base.relativize(originalPath).toString();
    return entryMap.containsKey(relativePath);
  }

  public void register(Path originalPath) {
    String relativePath = base.relativize(originalPath).toString();
    long size = -1;
    try {
      size = Files.size(originalPath);
    } catch (IOException e) {
      log.error("Unable to get file size of {} ({})", originalPath, e.getMessage());
    }
    entryMap.put(relativePath, new LogEntry(relativePath, size));
  }

  public Collection<LogEntry> getLogEntries() {
    return entryMap.values();
  }

  public void setLogEntries(Collection<LogEntry> logEntries) {
    entryMap = logEntries.stream().collect(Collectors.toMap(LogEntry::getPath, Function.identity()));
  }

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  @ToString
  public static class LogEntry {
    private String path;
    private long size;
  }
}
