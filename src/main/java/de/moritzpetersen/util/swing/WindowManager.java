package de.moritzpetersen.util.swing;

import static java.lang.Integer.parseInt;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.*;

public class WindowManager {
  private static final Path PARENT_PATH =
      Path.of(
          System.getProperty("user.home"),
          "Library",
          "Application Support",
          WindowManager.class.getName());

  public static void apply(Component comp) {
    load(comp);
    comp.addComponentListener(
        new ComponentAdapter() {
          @Override
          public void componentResized(ComponentEvent e) {
            save(e.getComponent());
          }

          @Override
          public void componentMoved(ComponentEvent e) {
            save(e.getComponent());
          }
        });
  }

  private static void save(Component comp) {
    try {
      Path path = getPath(comp);
      Files.createDirectories(path.getParent());
      if (Files.isWritable(path) || !Files.exists(path)) {
        Rectangle bounds = comp.getBounds();
        Files.writeString(
            path, "%d,%d,%d,%d".formatted(bounds.x, bounds.y, bounds.width, bounds.height));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void load(Component comp) {
    try {
      Path path = getPath(comp);
      if (Files.exists(path) && Files.isReadable(path)) {
        String data = Files.readString(path);
        if (data != null) {
          String[] split = data.split(",");
          if (split.length == 4) {
            comp.setBounds(
                parseInt(split[0]), parseInt(split[1]), parseInt(split[2]), parseInt(split[3]));
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static Path getPath(Component comp) {
    return PARENT_PATH.resolve(comp.getClass().getName());
  }
}
