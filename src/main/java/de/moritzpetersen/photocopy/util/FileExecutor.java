package de.moritzpetersen.photocopy.util;

import de.moritzpetersen.photocopy.config.Config;
import de.moritzpetersen.photocopy.copy.CopyLog;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Stream;
import javax.inject.Inject;

public class FileExecutor {
  @Inject private Config config;
  private CopyLog copyLog;

  /**
   * In order to handle exceptions properly, this method has to be used instead of {@link
   * Files#walk(Path, FileVisitOption...)} or {@link Files#walkFileTree(Path, FileVisitor)}
   *
   * @param path The base path.
   * @param processors The {@link FileProcessor}s that is applied on every file.
   */
  public void safeWalk(Path path, Collection<FileProcessor> processors) {
    copyLog = new CopyLog(path);
    for (FileProcessor processor : processors) {
      processor.setup(path);
    }
    safeWalkInternal(path, processors);
    for (FileProcessor processor : processors) {
      processor.shutdown(path);
    }
  }

  private void safeWalkInternal(Path path, Collection<FileProcessor> processors) {
    if (Files.isReadable(path)) {
      if (Files.isRegularFile(path) && FileExtension.isSupported(path)) {
        if (!config.isAvoidDuplicates() || !copyLog.exists(path)){
          for (FileProcessor processor : processors) {
            processor.process(path);
          }
          copyLog.register(path);
        }
      }
      if (Files.isDirectory(path)) {
        try (Stream<Path> list = Files.list(path)) {
          list.forEach(file -> safeWalkInternal(file, processors));
        } catch (IOException e) {
          System.err.printf("Error: %s (%s)%n", path, e.getMessage());
          // ignore
        }
      }
    }
  }
}
