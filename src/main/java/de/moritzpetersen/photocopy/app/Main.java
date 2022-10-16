package de.moritzpetersen.photocopy.app;

import de.moritzpetersen.macos.MacosUtils;
import de.moritzpetersen.photocopy.config.Config;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.awt.TrayIcon;

public class Main {
  public static void main(String[] args) throws Exception {
    System.setProperty("apple.awt.UIElement", "true");
    final String style =
        MacosUtils.isMacMenuBarDarkMode().map(isDarkMode -> isDarkMode ? "_dark" : "").orElse("");
    final URL iconUrl = ClassLoader.getSystemResource("icons/menubar" + style + ".png");
    final TrayIcon trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage(iconUrl));
    SystemTray.getSystemTray().add(trayIcon);
  }
}
