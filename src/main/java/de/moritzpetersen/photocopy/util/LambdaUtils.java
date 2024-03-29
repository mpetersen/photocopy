package de.moritzpetersen.photocopy.util;

import java.util.function.Consumer;
import java.util.function.Function;
import javafx.application.Platform;
import lombok.NonNull;

public class LambdaUtils {

  public static <U, V> Function<U, V> sneaky(ThrowingFunction<U, V> fn) {
    return value -> {
      try {
        return fn.apply(value);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    };
  }

  public static void runAsync(Runnable runnable) {
    new Thread(runnable).start();
  }

  @NonNull
  public static <T> Consumer<T> runLater(Consumer<T> consumer) {
    return value -> Platform.runLater(() -> consumer.accept(value));
  }

  public static void runLater(Runnable runnable) {
    Platform.runLater(runnable);
  }

  @FunctionalInterface
  public interface ThrowingFunction<T, R> {
    R apply(T value) throws Exception;
  }
}
