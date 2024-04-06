package de.moritzpetersen.photocopy.util;

import java.util.function.Consumer;
import java.util.function.Function;
import javax.swing.*;

public class LambdaUtils {
  public static <T, R> Function<T, R> sneaky(ThrowingFunction<T, R> fn) {
    return value -> {
      try {
        return fn.apply(value);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    };
  }

  public static <T> Consumer<T> sneaky(ThrowingConsumer<T> consumer) {
    return value -> {
      try {
        consumer.accept(value);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    };
  }

  public static <T> T sneaky(ThrowingSupplier<T> supplier) {
    try {
      return supplier.get();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void runAsync(Runnable runnable) {
    new Thread(runnable).start();
  }

  public static <T> Consumer<T> updateSwing(Consumer<T> consumer) {
    return value -> SwingUtilities.invokeLater(() -> consumer.accept(value));
  }

  public static void updateSwing(Runnable runnable) {
    SwingUtilities.invokeLater(runnable);
  }

  @FunctionalInterface
  public interface ThrowingFunction<T, R> {
    R apply(T value) throws Exception;
  }

  @FunctionalInterface
  public interface ThrowingConsumer<T> {
    void accept(T value) throws Exception;
  }

  @FunctionalInterface
  public interface ThrowingSupplier<T> {
    T get() throws Exception;
  }
}
