package de.moritzpetersen.photocopy.metadata;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.TimeZone;

public class PhotoMetadata {

  private static final int TAG_DATE_TIME = 306;
  private static final int TAG_DATE_TIME_ORIGINAL = 36867;
  private static final int TAG_TIME_ZONE = 36880;

  @Getter private final LocalDateTime dateTime;

  public PhotoMetadata(Path path) throws IOException, ImageProcessingException {
    this(Files.newInputStream(path));
  }

  public PhotoMetadata(InputStream in) throws ImageProcessingException, IOException {
    try (in) {
      Metadata metadata = ImageMetadataReader.readMetadata(in);
      ExifIFD0Directory directory =
          metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

      Instant instant = directory.getDate(TAG_DATE_TIME, TimeZone.getDefault()).toInstant();
      dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
  }
}
