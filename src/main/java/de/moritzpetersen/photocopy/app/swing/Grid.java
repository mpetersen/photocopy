package de.moritzpetersen.photocopy.app.swing;

import java.awt.*;

public class Grid {
  private int row = 0;
  private final int cols;
  private final int rows;
  private final int padding;
  private final int hGap;
  private final int vGap;

  public Grid(int cols, int rows, int padding, int hGap, int vGap) {
    this.cols = cols;
    this.rows = rows;
    this.padding = padding;
    this.hGap = hGap;
    this.vGap = vGap;
  }

  public Constraints getConstraints(int col) {
    return getConstraints(col, 1);
  }

  public Constraints getConstraints(int col, int colSpan) {
    return getConstraints(col, colSpan, GridBagConstraints.LINE_START);
  }

  public Constraints getConstraints(int col, int colSpan, int anchor) {
    return getConstraints(col, colSpan, anchor, GridBagConstraints.NONE);
  }

  public Constraints getConstraints(int col, int colSpan, int anchor, int fill) {
    Constraints constraints = new Constraints();
    constraints.gridx = col;
    constraints.gridy = 0;
    constraints.gridwidth = colSpan;
    constraints.anchor = anchor;
    constraints.fill = fill;
    constraints.isLastCol = col + colSpan == cols;
    int left = col == 0 ? padding : hGap;
    int right = constraints.isLastCol ? padding : 0;
    constraints.insets = new Insets(vGap, left, 0, right);
    return constraints;
  }

  public class Constraints extends GridBagConstraints {
    private boolean isLastCol = false;

    public GridBagConstraints get() {
      gridy = row;

      if (gridy == 0) {
        insets.top = padding;
        insets.bottom = 0;
      } else if (gridy == rows - 1) {
        insets.top = vGap;
        insets.bottom = padding;
      } else {
        insets.top = vGap;
        insets.bottom = 0;
      }

      if (isLastCol) {
        row++;
      }

      return this;
    }
  }
}
