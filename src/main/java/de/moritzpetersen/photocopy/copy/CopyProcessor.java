package de.moritzpetersen.photocopy.copy;

import com.drew.imaging.ImageProcessingException;
import de.moritzpetersen.photocopy.metadata.PhotoMetadata;
import de.moritzpetersen.photocopy.version.VersionedPath;

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
import java.util.stream.Stream;

public class CopyProcessor {
  public void doCopy(Path source, Path target, CopyStats stats, DateTimeFormatter formatter) throws InterruptedException, ExecutionException, IOException {
    ExecutorService threadPool =
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    Set<Future<PhotoCopy>> futures = new HashSet<>();
    Uniqueness uniqueness = new Uniqueness();
    try (Stream<Path> files = Files.walk(source)) {
      files
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
                          } catch (ImageProcessingException e) {
                            // ignore
                          } catch (Exception e) {
                            System.err.println(sourceFile);
                            e.printStackTrace();
                          }
                          return photoCopy;
                        });
                futures.add(future);
              });
    }
    for (Future<PhotoCopy> future : futures) {
      PhotoCopy photoCopy = future.get();
      stats.addStats(photoCopy.getBytesCopied());
    }
    threadPool.shutdown();
    stats.done();
  }
}
