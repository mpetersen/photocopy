package de.moritzpetersen.photocopy.app;

import de.moritzpetersen.macos.LaunchAgentService;
import de.moritzpetersen.photocopy.config.Config;
import de.moritzpetersen.photocopy.copy.CopyProcessor;
import de.moritzpetersen.photocopy.copy.CopyStats;
import de.moritzpetersen.photocopy.volume.Volume;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

import static de.moritzpetersen.macos.MacosUtils.open;
import static de.moritzpetersen.photocopy.util.StringUtils.ifNull;

public class Controller {
  @Inject private Config config;
  @Inject private LaunchAgentService launchAgentService;
  @Inject private CopyProcessor copyProcessor;

  public void onTargetDirectory(ActionEvent e) {
    int modifiers = e.getModifiers();
    if (modifiers == 1) {
      open(config.getTarget());
    } else {
      System.setProperty("apple.awt.fileDialogForDirectories", "true");
      FileDialog fd = new FileDialog((Frame) null, "Select target directory");
      fd.setVisible(true);
      String directory = fd.getDirectory();
      String file = fd.getFile();
      System.setProperty("apple.awt.fileDialogForDirectories", "false");

      if (directory != null && file != null) {
        Path targetDirectory = Path.of(directory, file);
        ((MenuItem) e.getSource()).setLabel(targetDirectory.toString());
        config.setTarget(targetDirectory);
        config.save();
      }
    }
  }

  public ItemListener onCheck(BiConsumer<Config, Boolean> configConsumer) {
    return e -> {
      boolean enabled = ((CheckboxMenuItem) e.getSource()).getState();
      configConsumer.accept(config, enabled);
      config.save();
    };
  }

  public void onFormatStr(ActionEvent e) {
    String formatStr =
        JOptionPane.showInputDialog(
            "Please enter file name format:", ifNull(config.getFormatStr(), ""));
    if (formatStr != null) {
      config.setFormatStr(formatStr);
      config.save();
      ((MenuItem) e.getSource()).setLabel(formatStr);
    }
  }

  public void onLaunchAtLogin(ItemEvent e) {
    boolean launchAtLogin = ((CheckboxMenuItem) e.getSource()).getState();
    launchAgentService.setLaunchAtLogin(launchAtLogin);
  }

  public ActionListener onVolume(Volume volume) {
    return e -> {
      if (e.getModifiers() == 1) {
        open(volume.toPath());
      } else {
        final MenuItem item = (MenuItem) e.getSource();
        final CopyStats copyStats =
            new CopyStats() {
              private int currentFile = 0;

              @Override
              public void setCount(long count) {
                super.setCount(count);
                updateLabel();
              }

              @Override
              public void addStats(long bytesCopied) {
                super.addStats(bytesCopied);
                currentFile++;
                updateLabel();
              }

              private void updateLabel() {
                item.setLabel(volume.getName() + " (" + currentFile + "/" + getCount() + ")");
              }
            };
        new Thread(
                () -> {
                  try {
                    item.setEnabled(false);
                    item.setLabel(volume.getName() + " (initializing…)");
                    copyProcessor.doCopy(volume, config, copyStats);
                  } catch (IOException | InterruptedException | ExecutionException ex) {
                    throw new RuntimeException(ex);
                  } finally {
                    item.setLabel(volume.getName());
                    item.setEnabled(true);
                  }
                })
            .start();
      }
    };
  }
}
