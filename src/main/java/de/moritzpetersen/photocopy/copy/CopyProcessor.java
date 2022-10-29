package de.moritzpetersen.photocopy.copy;

import de.moritzpetersen.macos.MacosUtils;
import de.moritzpetersen.photocopy.config.Config;
import de.moritzpetersen.photocopy.metadata.PhotoMetadata;
import de.moritzpetersen.photocopy.util.DeleteFileVisitor;
import de.moritzpetersen.photocopy.version.VersionedPath;
import de.moritzpetersen.photocopy.volume.Volume;
import de.moritzpetersen.photocopy.volume.VolumeService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitor;
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
import java.util.function.Consumer;

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
    doCopy(source, target, stats, formatter, config.isAvoidDuplicates());
    if (config.isOpenAfterCopy()) {
      MacosUtils.open(target);
    }
  }

  public void doCopy(Path source, Path target, CopyStats stats, DateTimeFormatter formatter, boolean avoidDuplicates)
      throws InterruptedException, ExecutionException, IOException {

    CopyLog copyLog = new CopyLog(source);

    class FileCounter implements Consumer<Path> {
      @Getter private long count;

      @Override
      public void accept(Path path) {
        count++;
      }
    }

    FileCounter counter = new FileCounter();

    walk(source, counter);

    stats.setCount(counter.getCount());

    ExecutorService threadPool =
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    Set<Future<PhotoCopy>> futures = new HashSet<>();
    Uniqueness uniqueness = new Uniqueness();
    walk(
        source,
        sourceFile -> {
          if (!(avoidDuplicates && copyLog.exists(sourceFile))) {
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
                        copyLog.register(sourceFile);
                        stats.addStats(photoCopy.getBytesCopied());
                        log.info("Done: " + sourceFile + " -> " + targetFile);
                      } catch (Exception e) {
                        log.error("Copy failed: {} ({})", sourceFile, e.getMessage());
                      }
                      return photoCopy;
                    });
            futures.add(future);
          }
        });
    for (Future<PhotoCopy> future : futures) {
      PhotoCopy photoCopy = future.get();
    }
    threadPool.shutdown();
    copyLog.save();
    stats.done();
  }

  /**
   * In order to handle exceptions properly, this method has to be used instead of
   * {@link Files#walk(Path, FileVisitOption...)} or {@link Files#walkFileTree(Path, FileVisitor)}
   *
   * @param input The base path.
   * @param consumer The consumer that is applied on every file.
   */
  private void walk(Path input, Consumer<Path> consumer) {
    if (Files.isReadable(input)) {
      if (Files.isRegularFile(input)) {
        consumer.accept(input);
      }
      if (Files.isDirectory(input)) {
        try {
          Files.list(input).forEach( path -> walk(path, consumer));
        } catch (IOException e) {
          log.error("Unable to process {} ({})", input, e.getMessage());
        }
      }
    }
  }
}
