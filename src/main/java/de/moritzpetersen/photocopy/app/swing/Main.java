package de.moritzpetersen.photocopy.app.swing;


import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLaf;
import de.moritzpetersen.factory.Factory;
import java.awt.*;
import javax.swing.*;

public class Main {
  public static void main(String[] args) {
    System.setProperty("apple.laf.useScreenMenuBar", "true");
    System.setProperty("apple.awt.application.appearance", "system");
    System.setProperty("apple.awt.application.name", "PhotoCopy");
    FlatLaf.registerCustomDefaultsSource("de.moritzpetersen.themes");
    FlatDarculaLaf.setup();

    Factory.create(MainWindow.class);
  }
}
