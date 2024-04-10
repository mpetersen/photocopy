package de.moritzpetersen.photocopy.app.fileList;


import de.moritzpetersen.photocopy.app.mainWindow.LabelBorder;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

public class AlternatingRowCellRenderer extends DefaultListCellRenderer {

  public static final Color ALT_ROW_COLOR = UIManager.getColor("PhotoCopy.list.alternateRow");
  public static final Border BORDER = new LabelBorder();

  @Override
  public Component getListCellRendererComponent(
      JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
    JComponent component =
        (JComponent)
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    if (index % 2 == 1) {
      component.setBackground(ALT_ROW_COLOR);
    }
    component.setBorder(BORDER);
    return component;
  }
}
