package de.moritzpetersen.photocopy.copy;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public class MyJsonClass extends JsonClass {
  @JsonProperty
  private Collection<String> data;
  @JsonProperty
  private String value;

  public MyJsonClass(InputStream in) throws IOException {
    super(in);
  }

  public Collection<String> getData() {
    return data;
  }

  public String getValue() {
    return value;
  }
}
