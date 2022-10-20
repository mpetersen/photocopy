package de.moritzpetersen.photocopy.copy;

import de.moritzpetersen.macos.MacosUtils;
import de.moritzpetersen.photocopy.config.Config;
import de.moritzpetersen.photocopy.metadata.PhotoMetadata;
import de.moritzpetersen.photocopy.util.DeleteFileVisitor;
import de.moritzpetersen.photocopy.version.VersionedPath;
import de.moritzpetersen.photocopy.volume.Volume;
import de.moritzpetersen.photocopy.volume.VolumeService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class CopyProcessor {

  @Inject private VolumeService volumeService;

  public void doCopy(Volume volume, Config config, CopyStats stats)
      throws IOException, ExecutionException, InterruptedException {
    doCopy(volume.toPath(), config, stats);
    if (config.isEjectEnabled()) {
      volumeService.unmount(volume);
    }
  }

  public void doCopy(Path source, Config config, CopyStats stats)
      throws IOException, ExecutionException, InterruptedException {
    if (source == null) {
      log.warn("Source not defined");
    }
    Path target = config.getTarget();
    if (target == null) {
      log.warn("Target not defined");
    }
    if (config.isEraseEnabled() && Files.exists(target)) {
      Files.walkFileTree(target, new DeleteFileVisitor());
    }
    if (!Files.exists(target)) {
      Files.createDirectories(target);
    }
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(config.getFormatStr());
    doCopy(source, target, stats, formatter);
    if (config.isOpenAfterCopy()) {
      MacosUtils.open(target);
    }
  }

  public void doCopy(Path source, Path target, CopyStats stats, DateTimeFormatter formatter)
      throws InterruptedException, ExecutionException, IOException {

    long count = Files.walk(source).filter(Files::isRegularFile).filter(Files::isRegularFile).count();
    stats.setCount(count);

    ExecutorService threadPool =
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    Set<Future<PhotoCopy>> futures = new HashSet<>();
    Uniqueness uniqueness = new Uniqueness();
    Files.walk(source)
        .filter(Files::isRegularFile)
        .filter(Files::isRegularFile)
        .forEach(
            sourceFile -> {
              Future<PhotoCopy> future =
                  threadPool.submit(
                      () -> {
                        PhotoCopy photoCopy = new PhotoCopy();
                        try {
                          PhotoMetadata metadata = new PhotoMetadata(sourceFile);
                          VersionedPath versionedPath =
                              new VersionedPath(target.resolve(sourceFile.getFileName()));
                          LocalDateTime dateTime = metadata.getDateTime();
                          String name = dateTime.format(formatter);
                          versionedPath.setName(name);

                          Path targetFile =
                              uniqueness.toPath(versionedPath, new SizeComparator(sourceFile));

                          if (targetFile != null) {
                            photoCopy.doCopy(sourceFile, targetFile);
                          }
                          stats.addStats(photoCopy.getBytesCopied());
                          log.info("Done: " + sourceFile + " -> " + targetFile);
                        } catch (Exception e) {
                          log.error("Copy failed: {} ({})", sourceFile, e.getMessage());
                        }
                        return photoCopy;
                      });
              futures.add(future);
            });
    for (Future<PhotoCopy> future : futures) {
      PhotoCopy photoCopy = future.get();
    }
    threadPool.shutdown();
    stats.done();
  }
}
