package de.moritzpetersen.macos;

import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class MacosUtils {
  /**
   * See: http://stackoverflow.com/a/33477375/1277252
   *
   * @return true if <code>defaults read -g AppleInterfaceStyle</code> has an exit status of <code>0
   *     </code> (i.e. _not_ returning "key not found").
   */
  public static Optional<Boolean> isMacMenuBarDarkMode() {
    try {
      // check for exit status only. Once there are more modes than "dark" and "default", we might
      // need to analyze string contents..
      final Process proc =
          Runtime.getRuntime().exec(new String[] {"defaults", "read", "-g", "AppleInterfaceStyle"});
      proc.waitFor(100, TimeUnit.MILLISECONDS);
      return Optional.of(proc.exitValue() == 0);
    } catch (IOException | InterruptedException | IllegalThreadStateException e) {
      // IllegalThreadStateException thrown by proc.exitValue(), if process didn't terminate
      return Optional.empty();
    }
  }

  @SneakyThrows
  public static void open(Path path) {
    Runtime.getRuntime().exec(new String[] {"open", path.toString()});
  }
}
