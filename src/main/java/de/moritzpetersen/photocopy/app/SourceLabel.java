package de.moritzpetersen.photocopy.app;

import java.awt.*;
import javax.swing.*;

public class SourceLabel extends JLabel {
  public SourceLabel(String text) {
    super(text);
    setFont(getFont().deriveFont(Font.BOLD));
    setBorder(new LabelBorder());
  }
}
