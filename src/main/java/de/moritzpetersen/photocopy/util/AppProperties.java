package de.moritzpetersen.photocopy.util;

import lombok.Getter;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.util.Properties;

@Getter
public class AppProperties {
  private final String name;
  private final String version;
  private final String group;
  private final String id;

  @SneakyThrows
  public AppProperties() {
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
