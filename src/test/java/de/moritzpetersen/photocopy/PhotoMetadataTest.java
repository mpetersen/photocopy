package de.moritzpetersen.photocopy;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

class PhotoMetadataTest {

  private static final String PHOTO_FILE = "/Users/mpeterse/Desktop/tmp/DSC_5524.NEF";

  @Test
  void listMetadata() throws Exception {
    Metadata metadata = ImageMetadataReader.readMetadata(
        new File(PHOTO_FILE));
    metadata
        .getDirectories()
        .forEach(
            dir -> {
              dir.getTags()
                  .forEach(
                      tag -> {
                        System.out.println(dir.getName() + "." + tag.getTagName() + "(" + tag.getTagType() + "): " + tag.getDescription());
                      });
            });
  }

  @Test
  void verifyDate() throws ImageProcessingException, IOException {
    PhotoMetadata photoMetadata = new PhotoMetadata(Path.of(PHOTO_FILE));
    System.out.println("Date: " + photoMetadata.getDateTime());
  }
}