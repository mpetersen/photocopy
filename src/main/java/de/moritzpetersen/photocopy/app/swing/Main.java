package de.moritzpetersen.photocopy.app.swing;

import javax.swing.*;

public class Main {
  public static void main(String[] args)
      throws UnsupportedLookAndFeelException,
          ClassNotFoundException,
          InstantiationException,
          IllegalAccessException {
    System.setProperty("apple.awt.application.appearance", "system");
    UIManager.setLookAndFeel("org.violetlib.aqua.AquaLookAndFeel");

    JFrame frame = new JFrame("PhotoCopy");

    frame.add(new ConfigPanel());

    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }
}
