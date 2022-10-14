package de.moritzpetersen.photocopy.copy;

public class CopyStats {
  private static final long MEGABYTES = 1024 * 1024;
  private long bytesCopied = 0;
  private final long start = System.currentTimeMillis();
  private long finish = -1;

  public void addStats(long bytesCopied) {
    this.bytesCopied += bytesCopied;
  }

  public long getBytesCopied() {
    return bytesCopied;
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
