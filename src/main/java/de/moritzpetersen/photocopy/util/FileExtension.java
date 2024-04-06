package de.moritzpetersen.photocopy.util;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FileExtension {
  /**
   * Copied from <a href="https://github.com/drewnoakes/metadata-extractor/blob/9f4ad4ce396eb2a0d2d3ec1d730dcd5a374602e7/Source/com/drew/tools/ProcessAllImagesInFolderUtility.java#L177">metadata-extractor</a>
   */
  private static final Set<String> SUPPORTED_EXTENSIONS =
      new HashSet<>(
          Arrays.asList(
              "3fr", "3g2", "3gp", "ai", "arw", "avi", "avif", "bmp", "cam", "cr2", "cr3", "crw",
              "dcr", "dng", "eps", "fuzzed", "gif", "gpr", "heic", "heif", "ico", "j2c", "jp2",
              "jpeg", "jpf", "jpg", "jpm", "jxr", "kdc", "m2ts", "m2v", "m4a", "m4v", "mj2", "mov",
              "mp3", "mp4", "mpg", "mts", "nef", "orf", "pbm", "pcx", "pef", "pgm", "png", "pnm",
              "ppm", "psd", "raf", "rw2", "rwl", "srw", "tif", "tiff", "wav", "webp", "x3f"));

  public static boolean isSupported(Path path) {
    String fileName = path.getFileName().toString();
    int index = fileName.lastIndexOf(".");
    if (index > 0 && index <= fileName.length() - 4) {
      String extension = fileName.substring(index + 1).toLowerCase();
      return SUPPORTED_EXTENSIONS.contains(extension);
    }
    return false;
  }
}
