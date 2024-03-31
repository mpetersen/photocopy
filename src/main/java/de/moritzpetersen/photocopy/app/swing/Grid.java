package de.moritzpetersen.photocopy.app.swing;

import java.awt.*;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Grid {
  private final int cols;
  private final int rows;
  private final int padding;
  private final int hGap;
  private final int vGap;

  public Constraints getConstraints(int col, int colSpan) {
    Constraints constraints = new Constraints();
    constraints.gridy = col;
    constraints.gridwidth = colSpan;
    constraints.anchor = GridBagConstraints.LINE_START;
    int left = col == 0 ? padding : hGap;
    int right = col + colSpan == cols ? padding : 0;
    constraints.insets = new Insets(vGap, left, 0, right);
    return constraints;
  }

  public class Constraints extends GridBagConstraints {
    public GridBagConstraints atRow(int row) {
      if (row == 0) {
        insets.top = padding;
        insets.bottom = 0;
      } else if (row == rows - 1) {
        insets.top = vGap;
        insets.bottom = padding;
      } else {
        insets.top = vGap;
        insets.bottom = 0;
      }
      gridy = row;
      return this;
    }
  }
}
