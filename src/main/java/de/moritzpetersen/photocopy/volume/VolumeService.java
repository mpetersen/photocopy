package de.moritzpetersen.photocopy.volume;

import de.moritzpetersen.photocopy.util.SimpleElementHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class VolumeService {
    public void unmount(final Volume volume) {
        final String volumeName = volume.getName();
        try {
            final Process process = exec("diskutil", "eject", volumeName);
            final boolean success = process.waitFor(10, TimeUnit.SECONDS);
            if (!success) {
                volume.setEjectFailed(true);
            } else {
                final int exitValue = process.exitValue();
                if (exitValue != 0) {
                    volume.setEjectFailed(true);
                }
            }
        } catch (final IOException | InterruptedException e) {
            volume.setEjectFailed(true);
        }
    }

    private Process exec(String... args) throws IOException {
        return Runtime.getRuntime().exec(args);
    }

    public Collection<Volume> externalVolumes() {
        final Process process;
        try (final InputStream in = (process = exec("diskutil", "list", "-plist", "physical")).getInputStream()) {
            final int exitValue = process.waitFor();
            if (exitValue == 0) {
                final XMLReader xmlReader = XMLReaderFactory.createXMLReader();
                final Collection<Volume> volumes = new ArrayList<>();
                xmlReader.setContentHandler(new SimpleElementHandler() {
                    private boolean readArrayMode = false;

                    @Override
                    protected void element(final String localName, final String characters) {
                        switch (localName) {
                            case "key":
                                readArrayMode = characters.equals("VolumesFromDisks");
                                break;
                            case "string":
                                if (readArrayMode) {
                                    volumes.add(new Volume(characters));
                                }
                                break;
                            case "array":
                                readArrayMode = false;
                                break;
                        }
                    }
                });
                xmlReader.parse(new InputSource(in));
                return volumes;
            }
        } catch (IOException | InterruptedException | SAXException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
