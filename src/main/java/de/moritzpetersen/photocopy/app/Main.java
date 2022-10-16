package de.moritzpetersen.photocopy.app;

import de.moritzpetersen.photocopy.config.Config;

import java.io.IOException;

public class Main {
  public static void main(String[] args) throws IOException {
    Config config = Config.load();
    if (config.getTarget() == null) {
      Preferences preferences = new Preferences(config);
    }
  }
}
