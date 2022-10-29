package de.moritzpetersen.photocopy.copy;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class JsonClass {
  public JsonClass(InputStream in) throws IOException {
    new ObjectMapper().readerForUpdating(this).readValue(in);
  }
}
