package de.moritzpetersen.photocopy.app.textComponent;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.nio.file.Path;
import java.util.Arrays;
import javax.swing.*;

public class FilePicker extends JPanel implements TextComponent {

  private final JTextField textField;

  public FilePicker(int columns, int hGap) {
    super(new BorderLayout(hGap, 0));

    textField = new JTextField(columns);

    JButton button = new JButton("…");
    button.addActionListener(
        event -> {
          FileDialog fileDialog = new FileDialog((Frame) null);
          fileDialog.setMode(FileDialog.LOAD);
          fileDialog.setFile(textField.getText());
          System.setProperty("apple.awt.fileDialogForDirectories", "true");
          fileDialog.setVisible(true);
          String directory = fileDialog.getDirectory();
          String file = fileDialog.getFile();
          if (directory != null && file != null) {
            textField.setText(Path.of(directory, file).toString());
            KeyEvent e =
                new KeyEvent(
                    textField,
                    KeyEvent.KEY_FIRST,
                    System.currentTimeMillis(),
                    0,
                    KeyEvent.VK_UNDEFINED,
                    '\n');
            KeyListener[] keyListeners = textField.getKeyListeners();
            Arrays.stream(keyListeners).forEach(l -> l.keyTyped(e));
          }
        });

    add(textField, BorderLayout.CENTER);
    add(button, BorderLayout.LINE_END);
  }

  @Override
  public synchronized void addKeyListener(KeyListener l) {
    textField.addKeyListener(l);
  }

  @Override
  public void setTransferHandler(TransferHandler newHandler) {
    textField.setTransferHandler(newHandler);
  }

  public String getText() {
    return textField.getText();
  }

  @Override
  public void setText(String text) {
    textField.setText(text);
  }
}
