package de.moritzpetersen.photocopy.app.knownLocations;

import de.moritzpetersen.factory.Factory;
import de.moritzpetersen.photocopy.app.fileList.AlternatingRowCellRenderer;
import de.moritzpetersen.photocopy.app.fileList.NoSelectionModel;
import de.moritzpetersen.photocopy.app.layout.Grid;
import de.moritzpetersen.photocopy.config.Config;
import de.moritzpetersen.util.swing.WindowManager;
import java.awt.*;
import java.nio.file.Path;
import javax.swing.*;

public class KnownLocationsDialog extends JDialog {

  private final JList<?> list;
  private final JButton cancel;
  private final JButton clear;
  private final Config config;

  public KnownLocationsDialog(Frame owner) {
    super(owner, "Clear Known Locations", true);

    config = Factory.getInstance(Config.class);
    list = new JList<>(config.getKnownLocations().stream().map(Path::toAbsolutePath).toArray());
    list.setCellRenderer(new AlternatingRowCellRenderer());
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    list.setSelectionModel(new NoSelectionModel());
    cancel = new JButton("Cancel");
    cancel.addActionListener(event -> close());
    clear = new JButton("Clear");
    clear.addActionListener(event -> {
      config.getKnownLocations().clear();
      config.save();
      close();
    });

    Grid grid = new Grid(getContentPane())
        .withColumns(2)
        .withPadding(20)
        .withHGap(8)
        .withVGap(14);

    grid.fill().resize().spanColumns().add(new JScrollPane(list));
    grid.spanRows().resizeX().lastLineEnd().add(cancel);
    grid.spanRows().lastLineEnd().add(clear);

    pack();
    WindowManager.apply(this);
  }

  private void close() {
    setVisible(false);
    dispose();
  }

  public static void main(String[] args){
    new KnownLocationsDialog(null).setVisible(true);
    System.exit(0);
  }
}
