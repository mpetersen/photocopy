package de.moritzpetersen.photocopy.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Config {
  private static final String DEFAULT_CONFIG_FILE =
      System.getenv("HOME") + "/.photocopy/config.json";
  private static final ObjectMapper MAPPER = new ObjectMapper();

  @JsonProperty("renameOnCopy")
  private String formatStr;

  @JsonProperty private Path source;
  @JsonProperty private Path target;
  @JsonProperty private boolean openAfterCopy;

  @JsonProperty("eraseBeforeCopy")
  private boolean eraseEnabled;

  @JsonProperty("ejectAfterCopy")
  private boolean ejectEnabled;

  public static Config load() throws IOException {
    File configFile = new File(DEFAULT_CONFIG_FILE);
    if (configFile.exists()) {
      return MAPPER.readValue(configFile, Config.class);
    } else {
      return new Config();
    }
  }

  @SneakyThrows
  public void save() {
    MAPPER.writerWithDefaultPrettyPrinter().writeValue(new File(DEFAULT_CONFIG_FILE), this);
  }
}
