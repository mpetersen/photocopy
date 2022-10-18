package de.moritzpetersen.photocopy.util;

import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Getter
public class AppProperties {
  private final String name;
  private final String version;
  private final String group;
  private final String id;

  public AppProperties() throws IOException {
    try (InputStream in = ClassLoader.getSystemResourceAsStream("app.properties")) {
      Properties props = new Properties();
      props.load(in);
      name = props.getProperty("app.name");
      version = props.getProperty("app.version");
      id = props.getProperty("app.id");
      group = props.getProperty("app.group");
    }
  }
}
