package de.moritzpetersen.photocopy;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class PhotoMetadata {

  private static final int TAG_DATE_TIME = 306;
  @Getter private final Date dateTime;

  public PhotoMetadata(File file) throws ImageProcessingException, IOException {
    Metadata metadata = ImageMetadataReader.readMetadata(file);
    ExifIFD0Directory exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
    dateTime = exifIFD0Directory.getDate(TAG_DATE_TIME);
  }
}
