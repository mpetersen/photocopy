package de.moritzpetersen.macos;

import de.moritzpetersen.photocopy.util.AppProperties;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LaunchAgentService {
  private final Path launchAgentFile;

  public LaunchAgentService(final AppProperties appProperties) {
    final String fileName =
        String.format("%s.%s.plist", appProperties.getGroup(), appProperties.getId());
    final String launchAgentDir = System.getProperty("user.home") + "/Library/LaunchAgents/";
    launchAgentFile = Paths.get(launchAgentDir, fileName);
  }

  public boolean isLaunchAtLogin() {
    return Files.exists(launchAgentFile);
  }

  @SneakyThrows
  public void setLaunchAtLogin(boolean launchAtLogin) {
    if (launchAtLogin) {
      final String data = new String(ClassLoader.getSystemResourceAsStream("LaunchAgent.plist").readAllBytes());
      final String replaced = data.replace("$USER_DIR", System.getProperty("user.dir"));
      Files.writeString(launchAgentFile, replaced);
    } else {
      if (Files.exists(launchAgentFile)) {
        Files.delete(launchAgentFile);
      }
    }
  }
}
