package de.moritzpetersen.photocopy;

import java.io.File;

public class Main {
  public static void main(String[] args) throws Exception {
    String rawFile = "/Users/mpeterse/Desktop/IMG_2022-09-17_17-37-24.NEF";
    PhotoMetadata photoMetadata = new PhotoMetadata(new File(rawFile));
    System.out.println(photoMetadata.getDateTime());
  }
}
