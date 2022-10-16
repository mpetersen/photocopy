package de.moritzpetersen.photocopy;

import javax.swing.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

public class Prefs extends JDialog {
  private JPanel contentPane;
  private JButton buttonOK;
  private JButton buttonCancel;
  private JTextField textFieldRename;
  private JCheckBox checkBoxEject;
  private JTextField textFieldTarget;
  private JButton buttonTarget;
  private JCheckBox checkBoxErase;

  public Prefs() {
    setTitle("Photocopy");
    setContentPane(contentPane);
    setModal(true);
    getRootPane().setDefaultButton(buttonOK);

    buttonTarget.addActionListener(e -> {
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setDialogTitle("Select target directory");
      fileChooser.setDialogType(JFileChooser.DIRECTORIES_ONLY);
      fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      fileChooser.showDialog(buttonTarget, "Select");
      File selectedFile = fileChooser.getSelectedFile();
      textFieldTarget.setText(selectedFile.getAbsolutePath());
    });

    buttonOK.addActionListener(e -> onOK());

    buttonCancel.addActionListener(e -> onCancel());

    // call onCancel() when cross is clicked
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        onCancel();
      }
    });

    // call onCancel() on ESCAPE
    contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
  }

  public String getTarget() {
    return textFieldTarget.getText();
  }

  public String getRenameFormat() {
    return textFieldRename.getText();
  }

  public boolean isEjectEnabled() {
    return checkBoxEject.isSelected();
  }

  public boolean isEraseEnabled() {
    return checkBoxErase.isSelected();
  }

  private void onOK() {
    dispose();
  }

  private void onCancel() {
    // add your code here if necessary
    dispose();
  }

  public static void main(String[] args) {
    Prefs dialog = new Prefs();
    dialog.pack();
    dialog.setVisible(true);
    System.exit(0);
  }
}
