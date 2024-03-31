package de.moritzpetersen.photocopy.app.swing;

import static de.moritzpetersen.photocopy.util.LambdaUtils.updateSwing;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLaf;
import de.moritzpetersen.factory.Factory;
import de.moritzpetersen.photocopy.config.Config;
import de.moritzpetersen.photocopy.copy.CopyProcessor;
import de.moritzpetersen.photocopy.copy.CopyStats;
import de.moritzpetersen.photocopy.volume.Volume;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import javax.swing.*;

public class Main {
  public static void main(String[] args) {
    System.setProperty("apple.laf.useScreenMenuBar", "true");
    System.setProperty("apple.awt.application.appearance", "system");
    System.setProperty("apple.awt.application.name", "PhotoCopy");
    FlatLaf.registerCustomDefaultsSource("de.moritzpetersen.themes");
    FlatDarculaLaf.setup();

    FileList fileList = new FileList();
    ConfigPanel configPanel = new ConfigPanel();
    JButton importButton = new JButton("Run PhotoCopy");
    JProgressBar importProgress = new JProgressBar();

    importButton.addActionListener(
        event -> {
          Config config = Factory.getInstance(Config.class);
          CopyProcessor copyProcessor = Factory.getInstance(CopyProcessor.class);

          Path sourceDir = fileList.getBasePath();
          Volume sourceVolume = Volume.of(sourceDir);

          if (!config.getKnownLocations().contains(sourceDir)) {
            config.getKnownLocations().add(sourceDir);
            config.save();
          }

          importProgress.setMaximum(fileList.getModel().getSize());

          CopyStats stats =
              new CopyStats() {
                private int counter = 0;

                @Override
                public void addStats(long bytesCopied) {
                  super.addStats(bytesCopied);

                  updateSwing(() -> importProgress.setValue(counter++));
                }
              };

          try {
            if (sourceVolume != null) {
              sourceVolume.addEjectFailedListener(
                  b -> {
                    System.out.println("Eject failed: " + sourceVolume);
                  });
              copyProcessor.doCopy(sourceVolume, config, stats);
            } else {
              copyProcessor.doCopy(sourceDir, config, stats);
            }
            if (config.isQuitAfterImport()) {
              System.exit(0);
            }
          } catch (IOException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
          }
        });

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
    controlPanel.add(configPanel, c1.get());
    controlPanel.add(importButton, c2.get());
    controlPanel.add(importProgress, c3.get());

    frame.add(
        new JScrollPane(
            fileList,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER),
        BorderLayout.CENTER);
    frame.add(controlPanel, BorderLayout.LINE_END);

    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }
}
