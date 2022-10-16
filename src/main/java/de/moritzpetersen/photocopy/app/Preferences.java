package de.moritzpetersen.photocopy.app;

import de.moritzpetersen.photocopy.config.Config;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.Path;

public class Preferences extends JDialog {
  private JPanel contentPane;
  private JButton buttonOK;
  private JButton buttonCancel;
  private JTextField textFieldRename;
  private JCheckBox checkBoxEject;
  private JTextField textFieldTarget;
  private JButton buttonTarget;
  private JCheckBox checkBoxErase;

  public Preferences(Config config) {
    setTitle("Photocopy");
    setContentPane(contentPane);
    setModal(true);
    getRootPane().setDefaultButton(buttonOK);

    buttonTarget.addActionListener(e -> {
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setCurrentDirectory(new File(textFieldTarget.getText()));
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

    textFieldTarget.setText(config.getTarget().toString());
    checkBoxEject.setSelected(config.isEjectEnabled());
    checkBoxErase.setSelected(config.isEraseEnabled());
    textFieldRename.setText(config.getFormatStr());

    pack();
    setVisible(true);
  }

  public Path getTarget() {
    return Path.of(textFieldTarget.getText());
  }

  public String getFormatStr() {
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
}
