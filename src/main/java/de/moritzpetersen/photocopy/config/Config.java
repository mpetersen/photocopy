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

  @JsonProperty private Path target;
  @JsonProperty private boolean openAfterCopy;

  @JsonProperty("eraseBeforeCopy")
  private boolean eraseEnabled;

  @JsonProperty("ejectAfterCopy")
  private boolean ejectEnabled;

  public Config() throws IOException {
    File configFile = new File(DEFAULT_CONFIG_FILE);
    if (configFile.exists()) {
      MAPPER.readerForUpdating(this).readValue(configFile);
    }
  }

  @SneakyThrows
  public void save() {
    File configFile = new File(DEFAULT_CONFIG_FILE);
    File parentFile = configFile.getParentFile();
    if (!parentFile.exists()) {
      parentFile.mkdirs();
    }
    MAPPER.writerWithDefaultPrettyPrinter().writeValue(configFile, this);
  }
}
