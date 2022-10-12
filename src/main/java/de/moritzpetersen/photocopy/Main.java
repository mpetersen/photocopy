package de.moritzpetersen.photocopy;

import com.drew.imaging.ImageProcessingException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

public class Main {
  public static void main(String[] args) throws Exception {
    Path source = Path.of(args[0]);
    Path target = Path.of(args[1]);
    String formatStr = args[2];
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatStr);

    long start = System.currentTimeMillis();

    ExecutorService threadPool =
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    Set<Future<PhotoCopy>> futures = new HashSet<>();
    Uniqueness uniqueness = new Uniqueness();
    try (Stream<Path> files = Files.list(source)) {
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
    long bytesCopied = 0;
    for (Future<PhotoCopy> future : futures) {
      PhotoCopy photoCopy = future.get();
      bytesCopied += photoCopy.getBytesCopied();
    }
    threadPool.shutdown();
    System.out.printf("Copied %s bytes in %s ms", bytesCopied, System.currentTimeMillis() - start);
    System.exit(0);
  }
}
