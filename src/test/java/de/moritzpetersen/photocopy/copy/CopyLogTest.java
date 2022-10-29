package de.moritzpetersen.photocopy.copy;

import de.moritzpetersen.testing.AbstractFileTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class CopyLogTest extends AbstractFileTest {
  @Test
  void verifyWorkflow() throws IOException {
    Path copyLogPath = path("");

    asssertExists(false, copyLogPath);

    CopyLog copyLog1 = new CopyLog(copyLogPath);

    Path file1 = create("file1.txt", "Hello 1");
    Path file2 = create("file2.txt", "Hello 2");

    assertFalse(copyLog1.exists(file1));
    copyLog1.register(file1);
    assertTrue(copyLog1.exists(file1));
    assertFalse(copyLog1.exists(file2));
    copyLog1.register(file2);
    assertTrue(copyLog1.exists(file2));

    assertEquals(2, copyLog1.getLogEntries().size());

    asssertExists(false, copyLog1.getPath());
    copyLog1.save();
    asssertExists(true, copyLog1.getPath());

    CopyLog copyLog2 = new CopyLog(copyLogPath);
//    new ObjectMapper().readerForUpdating(copyLog2).readValue(newInputStream(copyLog1.getPath()));

    assertEquals(2, copyLog2.getLogEntries().size());

    assertTrue(copyLog2.exists(file1));
    assertTrue(copyLog2.exists(file2));
  }
}
