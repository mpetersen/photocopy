package de.moritzpetersen.photocopy.app.fileList;

import javax.swing.*;

public class NoSelectionModel extends DefaultListSelectionModel {
  @Override
  public void setAnchorSelectionIndex(int anchorIndex) {}

  @Override
  public void setLeadAnchorNotificationEnabled(boolean flag) {}

  @Override
  public void setLeadSelectionIndex(int leadIndex) {}

  @Override
  public void setSelectionInterval(int index0, int index1) {}
}
