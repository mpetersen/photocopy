package de.moritzpetersen.photocopy;

import de.moritzpetersen.factory.Factory;
import de.moritzpetersen.photocopy.config.Config;
import de.moritzpetersen.photocopy.copy.CopyProcessor;
import de.moritzpetersen.photocopy.copy.CopyStats;
import de.moritzpetersen.photocopy.volume.Volume;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import javax.inject.Inject;

public class Main {
  @Inject private Config config;
  @Inject private CopyProcessor copyProcessor;

  public static void main(String[] args)
      throws IOException, ExecutionException, InterruptedException {
    Factory.create(Main.class).run(args);
  }

  public void run(String[] args) throws IOException, ExecutionException, InterruptedException {
    CopyStats stats = new CopyStats();

    Path sourcePath = Path.of(args[0]);
    Volume sourceVolume = Volume.of(sourcePath);

    if (sourceVolume != null) {
      sourceVolume.addEjectFailedListener(
          b -> {
            System.out.println("Eject failed: " + sourceVolume);
          });
      copyProcessor.doCopy(sourceVolume, config, stats);
    } else {
      copyProcessor.doCopy(sourcePath, config, stats);
    }

    System.out.printf(
        "Copied %s bytes in %s s (%.2f MB/s)",
        stats.getBytesCopied(), stats.getDurationAsString(), stats.getThroughput());
    System.exit(0);
  }
}
