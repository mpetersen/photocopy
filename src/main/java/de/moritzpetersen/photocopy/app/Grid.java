package de.moritzpetersen.photocopy.app;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;

public class Grid {
  private final Container container;
  private int columns;
  private int padding;
  private int hGap;
  private int vGap;
  private boolean spanColumns;
  private boolean spanRows;
  private int x = 0, y = 0;
  private Set<Integer> spannedColumns = new HashSet<>();
  private boolean fillX;
  private boolean fillY;
  private double resizeX;
  private double resizeY;
  private int anchor;

  public Grid(Container container) {
    this.container = container;
    this.container.setLayout(new GridBagLayout());
  }

  public Grid add(JComponent component) {
    boolean isLastCol = x == columns -1 || spanColumns;
    boolean isLastRow = spanRows;

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = anchor == 0 ? GridBagConstraints.LINE_START : anchor;

    int top = y == 0 ? padding : vGap;
    int left = x == 0 ? padding : hGap;
    int bottom = isLastRow ? padding : 0;
    int right = isLastCol ? padding : 0;
    c.insets = new Insets(top, left, bottom, right);

    c.gridheight = isLastRow ? GridBagConstraints.REMAINDER : 1;
    c.gridwidth = isLastCol ? GridBagConstraints.REMAINDER : 1;

    if (fillX && fillY) {
      c.fill = GridBagConstraints.BOTH;
    } else if (fillX) {
      c.fill = GridBagConstraints.HORIZONTAL;
    } else if (fillY) {
      c.fill = GridBagConstraints.VERTICAL;
    }

    if (resizeX > 0) {
      c.weightx = resizeX;
    }
    if (resizeY > 0) {
      c.weighty = resizeY;
    }
    c.gridx = x;
    c.gridy = y;

    container.add(component, c);

    if (isLastCol) {
      x = 0;
      y++;
    } else {
      x++;
    }
    while (spannedColumns.contains(x)) {
      x++;
    }

    // reset
    spanRows = spanColumns = fillX = fillY = false;
    resizeX = resizeY = 0.0;
    anchor = 0;

    return this;
  }

  public Grid withColumns(int columns) {
    this.columns = columns;
    return this;
  }

  public Grid withPadding(int padding) {
    this.padding = padding;
    return this;
  }

  public Grid withHGap(int hGap) {
    this.hGap = hGap;
    return this;
  }

  public Grid withVGap(int vGap) {
    this.vGap = vGap;
    return this;
  }

  public Grid spanColumns() {
    spanColumns = true;
    return this;
  }

  public Grid add(String label) {
    return add(new JLabel(label));
  }

  public Grid spanRows() {
    spannedColumns.add(x);
    spanRows = true;
    return this;
  }

  public Grid fillX() {
    fillX = true;
    return this;
  }

  public Grid fillY() {
    fillY = true;
    return this;
  }

  public Grid fill() {
    return fillX().fillY();
  }

  public Grid resizeX() {
    resizeX = 1.0;
    return this;
  }

  public Grid resizeY() {
    resizeY = 1.0;
    return this;
  }

  public Grid resize() {
    return resizeX().resizeY();
  }

  public Grid lastLineEnd() {
    anchor = GridBagConstraints.LAST_LINE_END;
    return this;
  }
}
