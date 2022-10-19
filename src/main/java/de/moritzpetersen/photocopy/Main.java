package de.moritzpetersen.photocopy;

import de.moritzpetersen.factory.Factory;
import de.moritzpetersen.photocopy.config.Config;
import de.moritzpetersen.photocopy.copy.CopyProcessor;
import de.moritzpetersen.photocopy.copy.CopyStats;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

public class Main {
  @Inject private Config config;
  @Inject private CopyProcessor copyProcessor;

  public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
    Factory.create(Main.class).run(args);
  }

  public void run(String[] args) throws IOException, ExecutionException, InterruptedException {
    Path source = Path.of(args[0]);
    CopyStats stats = new CopyStats();

    copyProcessor.doCopy(source, config, stats);

    System.out.printf(
        "Copied %s bytes in %s s (%.2f MB/s)",
        stats.getBytesCopied(), stats.getDurationAsString(), stats.getThroughput());
    System.exit(0);
  }
}
