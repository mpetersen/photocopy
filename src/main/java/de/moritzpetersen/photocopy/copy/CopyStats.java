package de.moritzpetersen.photocopy.copy;

import lombok.Getter;
import lombok.Setter;

public class CopyStats {
  private static final long MEGABYTES = 1024 * 1024;
  @Getter
  private long bytesCopied = 0;
  @Getter @Setter
  private int count;
  private final long start = System.currentTimeMillis();
  private long finish = -1;

  public void addStats(long bytesCopied) {
    this.bytesCopied += bytesCopied;
  }

  public void done() {
    finish = System.currentTimeMillis();
  }

  public long getDuration() {
    return finish < start ? System.currentTimeMillis() - start : finish - start;
  }

  public String getDurationAsString() {
    long duration = getDuration();
    long millis = duration % 1000;
    duration /= 1000;
    return duration + "." + millis;
  }

  public float getThroughput() {
    return ((float) bytesCopied / MEGABYTES) / getDuration() * 1000f;
  }
}
