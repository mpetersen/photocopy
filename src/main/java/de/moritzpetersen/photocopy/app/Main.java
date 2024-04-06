package de.moritzpetersen.photocopy.app;

import static de.moritzpetersen.photocopy.util.LambdaUtils.runAsync;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLaf;
import de.moritzpetersen.factory.Factory;
import de.moritzpetersen.photocopy.config.Config;
import de.moritzpetersen.photocopy.copy.CopyExecutor;
import de.moritzpetersen.photocopy.copy.CopyStats;
import de.moritzpetersen.photocopy.util.FileExecutor;
import de.moritzpetersen.photocopy.util.FileProcessor;
import de.moritzpetersen.util.swing.DragAndDrop;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;

public class Main {
  @Inject private MainWindow mainWindow;
  @Inject private Config config;
  @Inject private FileExecutor fileExecutor;

  public static void main(String[] args) {
    System.setProperty("apple.laf.useScreenMenuBar", "true");
    System.setProperty("apple.awt.application.appearance", "system");
    System.setProperty("apple.awt.application.name", "PhotoCopy");
    FlatLaf.registerCustomDefaultsSource("de.moritzpetersen.themes");
    FlatDarculaLaf.setup();

    Factory.create(Main.class).run(args);
  }

  private void run(String[] args) {
    mainWindow.init();

    mainWindow.setVisible(true);

    config.getKnownLocations().stream()
          .peek(System.out::println)
          .filter(Files::exists)
          .filter(Files::isReadable)
          .findFirst()
          .ifPresent(
              path -> {
                Collection<FileProcessor> processors = new ArrayList<>();
                processors.add(new FileListUpdater());
                if (config.isImportKnownLocations()) {
                  processors.add(new CopyExecutor(config, new CopyStats()));
                }
                fileExecutor.safeWalk(path, processors);
              });

    DragAndDrop.enableDrop(
        mainWindow.getDropComponent(),
        path ->
            runAsync(
                () -> {
                  Collection<FileProcessor> processors = new ArrayList<>();
                  processors.add(new FileListUpdater());
                  if (config.isImportOnDrop()) {
                    processors.add(new CopyExecutor(config, new CopyStats()));
                  }
                  fileExecutor.safeWalk(path, processors);
                }));

    mainWindow.setOnAction(
        () -> {
          Path path = mainWindow.getBasePath();
          if (path != null) {
            fileExecutor.safeWalk(path, List.of(new CopyExecutor(config, new CopyStats())));
          }
        });
  }

  private class FileListUpdater implements FileProcessor {
    @Override
    public void setup(Path path) {
      mainWindow.disableActionButton();
      mainWindow.setBasePath(path);
      mainWindow.removeAllFiles();
    }

    @Override
    public void shutdown(Path path) {
      mainWindow.enableActionButton();
    }

    @Override
    public void process(Path path) {
      mainWindow.addFile(path);
    }
  }
}
