package de.moritzpetersen.photocopy.copy;

import de.moritzpetersen.macos.MacosUtils;
import de.moritzpetersen.photocopy.config.Config;
import de.moritzpetersen.photocopy.metadata.PhotoMetadata;
import de.moritzpetersen.photocopy.util.DeleteFileVisitor;
import de.moritzpetersen.photocopy.util.FileProcessor;
import de.moritzpetersen.photocopy.version.VersionedPath;
import de.moritzpetersen.photocopy.volume.Volume;
import de.moritzpetersen.photocopy.volume.VolumeService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CopyExecutor implements FileProcessor {
  private final Config config;
  private final CopyStats stats;
  private final DateTimeFormatter formatter;
  private CopyLog copyLog;
  private Uniqueness uniqueness;

  public CopyExecutor(Config config, CopyStats stats) {
    this.config = config;
    this.stats = stats;

    this.formatter = DateTimeFormatter.ofPattern(config.getFormatStr());
  }

  @Override
  public void process(Path sourceFile) {
    if (!(config.isAvoidDuplicates() && copyLog.exists(sourceFile))) {
      PhotoCopy photoCopy = new PhotoCopy();
      try {
        PhotoMetadata metadata = new PhotoMetadata(sourceFile);
        VersionedPath versionedPath = new VersionedPath(config.getTarget().resolve(sourceFile.getFileName()));
        LocalDateTime dateTime = metadata.getDateTime();
        String name = dateTime.format(formatter);
        versionedPath.setName(name);

        Path targetFile = uniqueness.toPath(versionedPath, new SizeComparator(sourceFile));

        if (targetFile != null) {
          photoCopy.doCopy(sourceFile, targetFile);
          log.info("{}", config.getTarget().relativize(targetFile));
        }
        copyLog.register(sourceFile);
        stats.addStats(photoCopy.getBytesCopied());
      } catch (Exception e) {
        log.error("Copy failed: {} ({})", sourceFile, e.getMessage());
      }
    }
  }

  @Override
  public void setup(Path path) {
    copyLog = new CopyLog(path);
    uniqueness = new Uniqueness();

    if (config.isEraseEnabled()) {
      try {
        Files.walkFileTree(config.getTarget(), new DeleteFileVisitor());
      } catch (IOException e) {
        log.error("Unable to delete target {}: {}", config.getTarget(), e.getMessage());
      }
    }
  }

  @Override
  public void shutdown(Path path) {
    copyLog.save();

    Set<Path> knownLocations = config.getKnownLocations();
    if (!knownLocations.contains(path)) {
      knownLocations.add(path);
      config.save();
    }

    if (config.isEjectEnabled()) {
      Volume volume = Volume.of(path);
      if (volume != null){
        VolumeService volumeService = new VolumeService();
        volumeService.unmount(volume);
      }
    }

    if (config.isOpenAfterCopy()) {
      MacosUtils.open(config.getTarget());
    }

    if (config.isQuitAfterImport()) {
      System.exit(0);
    }
  }
}
