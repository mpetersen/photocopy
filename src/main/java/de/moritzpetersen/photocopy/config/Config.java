package de.moritzpetersen.photocopy.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Getter
public class Config {
  private static final String DEFAULT_CONFIG_FILE = System.getenv("HOME") + "/.photocopy/config.json";

  @JsonProperty("format")
  private String formatStr;
  @JsonProperty
  private Path source;
  @JsonProperty
  private Path target;

  public static Config load() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(new File(DEFAULT_CONFIG_FILE), Config.class);
  }
}
