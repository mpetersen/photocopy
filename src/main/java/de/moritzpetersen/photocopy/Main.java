package de.moritzpetersen.photocopy;

import de.moritzpetersen.photocopy.config.Config;
import de.moritzpetersen.photocopy.copy.CopyProcessor;
import de.moritzpetersen.photocopy.copy.CopyStats;

import java.nio.file.Path;
import java.time.format.DateTimeFormatter;

public class Main {
  public static void main(String[] args) throws Exception {
    Config config = new Config();

    Path source = Path.of(args[0]);
    Path target = config.getTarget();
    String formatStr = config.getFormatStr();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatStr);

    CopyStats stats = new CopyStats();

    new CopyProcessor().doCopy(source, target, stats, formatter);

    System.out.printf(
        "Copied %s bytes in %s s (%.2f MB/s)",
        stats.getBytesCopied(), stats.getDurationAsString(), stats.getThroughput());
    System.exit(0);
  }
}
