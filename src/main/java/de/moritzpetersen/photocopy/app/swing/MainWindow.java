package de.moritzpetersen.photocopy.app.swing;

import static de.moritzpetersen.factory.Factory.inject;
import static de.moritzpetersen.photocopy.util.LambdaUtils.updateSwing;

import de.moritzpetersen.photocopy.config.Config;
import de.moritzpetersen.photocopy.copy.CopyProcessor;
import de.moritzpetersen.photocopy.copy.CopyStats;
import de.moritzpetersen.photocopy.volume.Volume;
import de.moritzpetersen.util.swing.WindowManager;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import javax.swing.*;

public class MainWindow extends JFrame {
  private static final String TITLE = "PhotoCopy";
  private final Config config = inject(Config.class);
  private final FileList fileList = inject(FileList.class);
  private final ConfigPanel configPanel = inject(ConfigPanel.class);
  private final CopyProcessor copyProcessor = inject(CopyProcessor.class);

  public MainWindow() throws HeadlessException {
    super(TITLE);

    JButton importButton = new JButton("Run " + TITLE);
    JProgressBar importProgress = new JProgressBar();

    importButton.addActionListener(
        event -> {
          Path sourceDir = fileList.getBasePath();
          Volume sourceVolume = Volume.of(sourceDir);

          if (!config.getKnownLocations().contains(sourceDir)) {
            config.getKnownLocations().add(sourceDir);
            config.save();
          }

          importProgress.setMaximum(fileList.getItemCount());

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
                  b -> System.out.println("Eject failed: " + sourceVolume));
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

    add(
        new JScrollPane(
            fileList,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER),
        BorderLayout.CENTER);
    add(controlPanel, BorderLayout.LINE_END);

    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    pack();
    WindowManager.apply(this);
    setVisible(true);
  }
}
