package de.moritzpetersen.photocopy.copy;

import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class PhotoCopy {

  @Getter
  private long bytesCopied;

  public void doCopy(Path sourceFile, Path targetFile) throws IOException {
    if (!Files.exists(targetFile) && Files.exists(sourceFile)) {
      Path parent = targetFile.getParent();
      if (!Files.exists(parent)) {
        Files.createDirectories(parent);
      }
      Files.copy(sourceFile, targetFile, StandardCopyOption.COPY_ATTRIBUTES);
      bytesCopied = Files.size(targetFile);
    }
  }
}
