package de.moritzpetersen.photocopy.copy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import lombok.Getter;

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
      Files.setLastModifiedTime(targetFile, Files.getLastModifiedTime(sourceFile));
      bytesCopied = Files.size(targetFile);
    }
  }
}
