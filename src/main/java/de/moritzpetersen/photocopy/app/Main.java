package de.moritzpetersen.photocopy.app;

import de.moritzpetersen.macos.LaunchAgentService;
import de.moritzpetersen.macos.MacosUtils;
import de.moritzpetersen.photocopy.config.Config;
import de.moritzpetersen.photocopy.copy.CopyProcessor;
import de.moritzpetersen.photocopy.copy.CopyStats;
import de.moritzpetersen.photocopy.util.AppProperties;
import de.moritzpetersen.photocopy.util.DeleteFileVisitor;
import de.moritzpetersen.photocopy.volume.Volume;
import de.moritzpetersen.photocopy.volume.VolumeService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

@Slf4j
public class Main {
  private static Collection<Volume> currentVolumes;

  public static void main(String[] args) throws Exception {
    System.setProperty("apple.awt.UIElement", "true");
    final String style =
        MacosUtils.isMacMenuBarDarkMode().map(isDarkMode -> isDarkMode ? "_dark" : "").orElse("");
    final URL iconUrl = ClassLoader.getSystemResource("icons/menubar" + style + ".png");

    final AppProperties appProperties = new AppProperties();
    final LaunchAgentService launchAgentService = new LaunchAgentService(appProperties);
    final VolumeService volumeService = new VolumeService();
    final Config config = new Config();

    final TrayIcon trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage(iconUrl));
    final PopupMenu trayMenu = new PopupMenu();
    trayMenu.addSeparator();
    trayMenu.add(item("Target Directory:"));
    trayMenu.add(
        item(
            config.getTarget() == null ? "Select…" : config.getTarget().toString(),
            e -> {
              int modifiers = e.getModifiers();
              if (modifiers != 0) {
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
            }));
    trayMenu.add(
        item(
            "Open after Copy",
            config.isOpenAfterCopy(),
            e -> {
              config.setOpenAfterCopy(((MenuItem) e.getSource()).isEnabled());
              config.save();
            }));
    trayMenu.addSeparator();
    trayMenu.add(item("Rename on Copy:"));
    trayMenu.add(
        item(
            config.getFormatStr(),
            label -> label == null || label.length() == 0 ? "Set Format…" : label,
            e -> {
              String value = config.getFormatStr() == null ? "" : config.getFormatStr();
              String formatStr =
                  JOptionPane.showInputDialog("Please enter file name format", value);
              if (formatStr != null) {
                config.setFormatStr(formatStr);
                config.save();
                ((MenuItem) e.getSource()).setLabel(formatStr);
              }
            }));
    trayMenu.addSeparator();
    trayMenu.add(
        item(
            "Eject after Copy",
            config.isEjectEnabled(),
            e -> {
              boolean ejectEnabled = ((CheckboxMenuItem) e.getSource()).isEnabled();
              config.setEjectEnabled(ejectEnabled);
              config.save();
            }));
    trayMenu.add(
        item(
            "Erase before Copy",
            config.isEraseEnabled(),
            e -> {
              boolean eraseEnabled = ((CheckboxMenuItem) e.getSource()).isEnabled();
              config.setEraseEnabled(eraseEnabled);
              config.save();
            }));
    trayMenu.addSeparator();
    trayMenu.add(
        item(
            "Launch " + appProperties.getName() + " at Login",
            launchAgentService.isLaunchAtLogin(),
            e -> {
              boolean launchAtLogin = ((CheckboxMenuItem) e.getSource()).isEnabled();
              launchAgentService.setLaunchAtLogin(launchAtLogin);
            }));
    trayMenu.addSeparator();
    trayMenu.add(
        item(
            "Quit " + appProperties.getName(),
            e -> {
              System.exit(0);
            }));
    final int initialItemCount = trayMenu.getItemCount();

    trayIcon.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mousePressed(MouseEvent e) {
            Collection<Volume> volumes = volumeService.externalVolumes();
            if (!volumes.equals(currentVolumes)) {
              while (trayMenu.getItemCount() > initialItemCount) {
                trayMenu.remove(0);
              }
              for (Volume volume : volumes) {
                trayMenu.insert(
                    item(
                        volume.getName(),
                        ev -> {
                          MenuItem item = (MenuItem) ev.getSource();
                          Path target = config.getTarget();
                          try {
                            if (target == null) {
                              log.info("Target not defined");
                              return;
                            }
                            item.setEnabled(false);
                            if (config.isEraseEnabled() && Files.exists(target)) {
                              Files.walkFileTree(target, new DeleteFileVisitor());
                            }
                            if (!Files.exists(target)) {
                              Files.createDirectories(target);
                            }
                            CopyProcessor processor = new CopyProcessor();
                            DateTimeFormatter formatter =
                                DateTimeFormatter.ofPattern(config.getFormatStr());
                            Path source = Path.of("/Volumes", volume.getName());
                            processor.doCopy(source, target, new CopyStats(), formatter);
                            if (config.isOpenAfterCopy()) {
                              open(config.getTarget());
                            }
                            if (config.isEjectEnabled()) {
                              volumeService.unmount(volume);
                            }
                          } catch (IOException | InterruptedException | ExecutionException ex) {
                            throw new RuntimeException(ex);
                          } finally {
                            item.setEnabled(true);
                          }
                        }),
                    0);
              }
              currentVolumes = volumes;
            }
          }
        });
    trayIcon.setPopupMenu(trayMenu);
    SystemTray.getSystemTray().add(trayIcon);
  }

  @SneakyThrows
  private static void open(Path path) {
    Runtime.getRuntime().exec(new String[] {"open", path.toString()});
  }

  private static MenuItem item(String label, boolean isEnabled, ItemListener itemListener) {
    CheckboxMenuItem item = new CheckboxMenuItem(label, isEnabled);
    item.addItemListener(
        e -> {
          EventQueue.invokeLater(() -> itemListener.itemStateChanged(e));
        });
    return item;
  }

  private static MenuItem item(String label) {
    return item(label, null);
  }

  private static MenuItem item(String label, ActionListener actionListener) {
    return item(label, Function.identity(), actionListener);
  }

  private static <T> MenuItem item(
      String label, Function<String, String> labelFn, ActionListener actionListener) {
    MenuItem item =
        new MenuItem() {
          @Override
          public synchronized void setLabel(String label) {
            super.setLabel(labelFn.apply(label));
          }
        };
    item.setLabel(label);
    if (actionListener == null) {
      item.setEnabled(false);
    } else {
      item.addActionListener(
          e -> {
            EventQueue.invokeLater(() -> actionListener.actionPerformed(e));
          });
    }
    return item;
  }

}
