package de.moritzpetersen.photocopy.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.moritzpetersen.photocopy.util.JsonFile;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@Slf4j
public class Config extends JsonFile {
  private static final String DEFAULT_CONFIG_FILE =
      System.getenv("HOME") + "/.photocopy/config.json";

  @JsonProperty("renameOnCopy")
  private String formatStr;

  @JsonProperty private Path target;
  @JsonProperty private boolean openAfterCopy;

  @JsonProperty("eraseBeforeCopy")
  private boolean eraseEnabled;

  @JsonProperty("ejectAfterCopy")
  private boolean ejectEnabled;

  @JsonProperty private boolean avoidDuplicates;

  @JsonProperty private boolean importOnDrop;

  @JsonProperty private boolean importKnownLocations;
  @JsonProperty private Set<Path> knownLocations = new HashSet<>();
  @JsonProperty private boolean quitAfterImport;

  public Config() {
    super(Path.of(DEFAULT_CONFIG_FILE));
  }
}
