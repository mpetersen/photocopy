package de.moritzpetersen.photocopy.util;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class FileUtils {
  /**
   * In order to handle exceptions properly, this method has to be used instead of
   * {@link Files#walk(Path, FileVisitOption...)} or {@link Files#walkFileTree(Path, FileVisitor)}
   *
   * @param input The base path.
   * @param consumer The consumer that is applied on every file.
   */
  public static void safeWalk(Path input, Consumer<Path> consumer) {
    if (Files.isReadable(input)) {
      if (Files.isRegularFile(input)) {
        consumer.accept(input);
      }
      if (Files.isDirectory(input)) {
        try (Stream<Path> list = Files.list(input)) {
          list.forEach(path -> safeWalk(path, consumer));
        } catch (IOException e) {
          // ignore
        }
      }
    }
  }

}
