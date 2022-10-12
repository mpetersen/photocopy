package de.moritzpetersen.photocopy;

public class DefaultVersioningStrategy implements VersioningStrategy {
  private int version = 1;

  @Override
  public void inc() {
    version++;
  }

  @Override
  public String toString() {
    return version < 2 ? "" : "-" + version;
  }
}
