package de.moritzpetersen.photocopy.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Getter
public class Config {
  private static final String DEFAULT_CONFIG_FILE =
      System.getenv("HOME") + "/.photocopy/config.json";
  private static final ObjectMapper MAPPER = new ObjectMapper();

  @JsonProperty("format")
  private String formatStr;

  @JsonProperty private Path source;
  @JsonProperty private Path target;

  @JsonProperty("erase")
  private boolean eraseEnabled;

  @JsonProperty("eject")
  private boolean ejectEnabled;

  public static Config load() throws IOException {
    File configFile = new File(DEFAULT_CONFIG_FILE);
    if (configFile.exists()) {
      return MAPPER.readValue(configFile, Config.class);
    } else {
      return new Config();
    }
  }

  public void save() throws IOException {
    MAPPER.writeValue(new File(DEFAULT_CONFIG_FILE), this);
  }
}
