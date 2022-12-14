package de.moritzpetersen.photocopy.app;

import de.moritzpetersen.factory.Factory;
import de.moritzpetersen.macos.LaunchAgentService;
import de.moritzpetersen.photocopy.config.Config;
import de.moritzpetersen.photocopy.util.AppProperties;
import de.moritzpetersen.photocopy.volume.Volume;
import de.moritzpetersen.photocopy.volume.VolumeService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

import static de.moritzpetersen.photocopy.util.StringUtils.ifEmpty;

@Slf4j
public class Main {
  private static Collection<Volume> currentVolumes;

  @Inject private Controller controller;

  @Inject private Config config;

  static {
    // Hide dock icon
    System.setProperty("apple.awt.UIElement", "true");
  }

  public static void main(String[] args) {
    EventQueue.invokeLater(() -> Factory.create(Main.class).run());
  }

  @SneakyThrows
  public void run() {
    final AppProperties appProperties = new AppProperties();
    final LaunchAgentService launchAgentService = new LaunchAgentService(appProperties);
    final VolumeService volumeService = new VolumeService();
    //    final Config config = new Config();

    //    final String style =
    //        MacosUtils.isMacMenuBarDarkMode().map(isDarkMode -> isDarkMode ? "_dark" :
    // "").orElse("");
    //    final URL iconUrl = ClassLoader.getSystemResource("icons/menubar" + style + ".png");
    final URL iconUrl = ClassLoader.getSystemResource("icons/menubar.png");

    final TrayIcon trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage(iconUrl));
    final PopupMenu trayMenu = new PopupMenu();
    trayMenu.add(item("Copy from (hold down ⇧ to open):"));
    trayMenu.add(
        item(
            "Avoid Duplicates",
            'D',
            config.isAvoidDuplicates(),
            controller.onCheck(Config::setAvoidDuplicates)));
    trayMenu.add(
        item(
            "Eject after Copy",
            'E',
            config.isEjectEnabled(),
            controller.onCheck(Config::setEjectEnabled)));
    trayMenu.addSeparator();
    trayMenu.add(item("Target Directory (hold down ⇧ to open):"));
    trayMenu.add(
        item(
            config.getTarget(),
            ifEmpty("Select Target Directory…"),
            'T',
            controller::onTargetDirectory));
    trayMenu.add(
        item(
            "Erase before Copy",
            'X',
            config.isEraseEnabled(),
            controller.onCheck(Config::setEraseEnabled)));
    trayMenu.add(
        item(
            "Open after Copy",
            'O',
            config.isOpenAfterCopy(),
            controller.onCheck(Config::setOpenAfterCopy)));
    trayMenu.addSeparator();
    trayMenu.add(item("Rename on Copy:"));
    trayMenu.add(item(config.getFormatStr(), ifEmpty("Set Format…"), 'R', controller::onFormatStr));
    trayMenu.addSeparator();
    trayMenu.add(
        item(
            "Launch " + appProperties.getName() + " at Login",
            'L',
            launchAgentService.isLaunchAtLogin(),
            controller::onLaunchAtLogin));
    trayMenu.addSeparator();
    trayMenu.add(item("Quit " + appProperties.getName(), 'Q', e -> System.exit(0)));
    final int initialItemCount = trayMenu.getItemCount();
    updateVolumes(currentVolumes, trayMenu, initialItemCount);

    trayIcon.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mousePressed(MouseEvent e) {
            Collection<Volume> volumes = volumeService.externalVolumes();
            if (hasChanged(volumes)) {
              updateVolumes(volumes, trayMenu, initialItemCount);
              currentVolumes = volumes;
            }
          }
        });
    trayIcon.setPopupMenu(trayMenu);
    SystemTray.getSystemTray().add(trayIcon);
  }

  private void updateVolumes(Collection<Volume> volumes, PopupMenu trayMenu, int initialItemCount) {
    while (trayMenu.getItemCount() > initialItemCount) {
      trayMenu.remove(1);
    }
    if (volumes == null || volumes.size() == 0) {
      trayMenu.insert(item("No device found"), 1);
    } else {
      int i = 0;
      for (Volume volume : volumes) {
        trayMenu.insert(item(volume.getName(), '1' + i++, controller.onVolume(volume)), 1);
      }
    }
  }

  private static boolean hasChanged(Collection<Volume> newVolumes) {
    final boolean bothAreEmpty = isEmpty(newVolumes) && isEmpty(currentVolumes);
    if (!bothAreEmpty) {
      return !newVolumes.equals(currentVolumes);
    } else {
      return false;
    }
  }

  private static boolean isEmpty(Collection<Volume> newVolumes) {
    return newVolumes == null || newVolumes.size() == 0;
  }

  private static MenuItem item(
      String label, int shortcut, boolean isEnabled, ItemListener itemListener) {
    CheckboxMenuItem item = new CheckboxMenuItem(label, isEnabled);
    item.setShortcut(new MenuShortcut(shortcut));
    item.addItemListener(itemListener);
    return item;
  }

  private static MenuItem item(Object label) {
    return item(label, -1, null);
  }

  private static MenuItem item(Object label, int shortcut, ActionListener actionListener) {
    return item(label, Function.identity(), shortcut, actionListener);
  }

  private static MenuItem item(
      Object label, Function<String, String> labelFn, int shortcut, ActionListener actionListener) {
    MenuItem item =
        new MenuItem() {
          @Override
          public synchronized void setLabel(String label) {
            super.setLabel(labelFn.apply(label));
          }
        };
    item.setLabel(label == null ? null : Objects.toString(label));
    if (shortcut != -1) {
      item.setShortcut(new MenuShortcut(shortcut));
    }
    if (actionListener == null) {
      item.setEnabled(false);
    } else {
      item.addActionListener(
          e ->
              new Thread(
                      () -> {
                        actionListener.actionPerformed(e);
                      })
                  .start());
    }
    return item;
  }
}
