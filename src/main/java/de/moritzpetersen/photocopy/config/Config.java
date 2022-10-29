package de.moritzpetersen.photocopy.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.moritzpetersen.photocopy.util.JsonFile;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@Slf4j
public class Config extends JsonFile {
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

  @JsonProperty private boolean avoidDuplicates;

  public Config() {
    super(Path.of(DEFAULT_CONFIG_FILE));
  }
}
