package de.moritzpetersen.photocopy.app.textComponent;

import java.awt.event.KeyListener;

public interface TextComponent {
  void addKeyListener(KeyListener keyListener);

  String getText();

  void setText(String text);
}
