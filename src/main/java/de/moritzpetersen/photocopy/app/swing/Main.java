package de.moritzpetersen.photocopy.app.swing;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLaf;
import java.awt.*;
import javax.swing.*;

public class Main {
  public static void main(String[] args) {
    System.setProperty("apple.laf.useScreenMenuBar", "true");
    System.setProperty("apple.awt.application.appearance", "system");
    System.setProperty("apple.awt.application.name", "PhotoCopy");
    FlatLaf.registerCustomDefaultsSource("de.moritzpetersen.themes");
    FlatDarculaLaf.setup();

    JFrame frame = new JFrame("PhotoCopy");

    JPanel controlPanel = new JPanel();
    controlPanel.setLayout(new GridBagLayout());
    Grid grid = new Grid(1, 3, 10, 8, 14);
    Grid.Constraints c1 = grid.getConstraints(0);
    Grid.Constraints c2 = grid.getConstraints(0);
    c2.anchor = GridBagConstraints.LAST_LINE_END;
    c2.weighty = 1.0;
    Grid.Constraints c3 = grid.getConstraints(0);
    c3.fill = GridBagConstraints.HORIZONTAL;
    controlPanel.add(new ConfigPanel(), c1.get());
    controlPanel.add(new JButton("Run PhotoCopy"), c2.get());
    controlPanel.add(new JProgressBar(), c3.get());

    frame.add(
        new JScrollPane(
            new FileList(),
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER),
        BorderLayout.CENTER);
    frame.add(controlPanel, BorderLayout.LINE_END);

    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }
}
