package de.moritzpetersen.photocopy.util;

import java.util.function.Function;

public class StringUtils {
  public static <T> String ifNull(T obj, String defaultValue, Function<T, String> fn) {
    if (obj == null) {
      return defaultValue;
    }
    return fn.apply(obj);
  }

  public static <T> String ifNull(T obj, String defaultValue) {
    return ifNull(obj, defaultValue, Object::toString);
  }

  public static Function<String, String> ifEmpty(String defaultValue) {
    return str -> str == null || str.trim().length() == 0 ? defaultValue : str;
  }
}
