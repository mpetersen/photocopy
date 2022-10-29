package de.moritzpetersen.photocopy.copy;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MyJsonClassTest {

  @Test
  void loadJson() throws IOException {
    String jsonStr = """
        {
          "value": "Hello, world!",
          "data": [
            "foo",
            "bar"
          ]
        }
        """;
    MyJsonClass myJsonClass = new MyJsonClass(new ByteArrayInputStream(jsonStr.getBytes()));
    assertEquals("Hello, world!", myJsonClass.getValue());
    assertEquals(2, myJsonClass.getData().size());
  }
}