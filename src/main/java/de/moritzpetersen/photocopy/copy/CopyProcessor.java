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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

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
    final Map<Path, PhotoMetadata> sourceFiles = new HashMap<>();
    try (Stream<Path> files = Files.walk(source)) {
      files
          .filter(Files::isRegularFile)
          .filter(Files::isReadable)
          .forEach(
              sourceFile -> {
                try {
                  PhotoMetadata metadata = new PhotoMetadata(sourceFile);
                  sourceFiles.put(sourceFile, metadata);
                } catch (Exception e) {
                  log.warn("Metadata not available: {}", sourceFile);
                }
              });
    }
    stats.setCount(sourceFiles.size());

    ExecutorService threadPool =
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    Set<Future<PhotoCopy>> futures = new HashSet<>();
    Uniqueness uniqueness = new Uniqueness();
    sourceFiles.forEach((sourceFile, metadata) -> {
      Future<PhotoCopy> future =
          threadPool.submit(
              () -> {
                PhotoCopy photoCopy = new PhotoCopy();
                try {
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
                  log.info("Done: " + sourceFile + " -> " + targetFile);
                } catch (Exception e) {
                  log.error("Copy failed: {}", sourceFile);
                }
                return photoCopy;
              });
      futures.add(future);
    });
    for (Future<PhotoCopy> future : futures) {
      PhotoCopy photoCopy = future.get();
      stats.addStats(photoCopy.getBytesCopied());
    }
    threadPool.shutdown();
    stats.done();
  }
}
