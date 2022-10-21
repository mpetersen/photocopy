# ${project.name}

${project.name} is an application that helps you with your photography workflow. It copies photos from your SD card and renames them on the fly.

My usual workflow was that I imported photos directly in Adobe Lightroom (Classic), then renamed photos by their capture date. Now I switched to Lightroom Desktop, which does not have a rename feature. I can still rename photos, when they are synched with Lightroom Classic, but I want to skip that step. ${project.name} helps me with that. It renames the photos, copies them to my hard drive and also ejects the SD card after copy.

## Usage

${project.name} can be used either as a command line application or as a macOS application in the menubar. 

### Command line application

Create a configuration file in `~/.${project.artifactId}/config.json`. The file needs to have the following structure:

```json
{
  "target" : "/path/to/target/directory",
  "openAfterCopy" : true,
  "renameOnCopy" : "'IMG'_yyyy-MM-dd_HH-mm-ss",
  "eraseBeforeCopy" : true,
  "ejectAfterCopy" : true
}
```

| Field    | Description                                                                                                                                                           |
|----------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `target` | The path of the target directory.                                                                                                                                     |
| `openAfterCopy` | If `true` then the target directory will be opened after copy.                                                                                                        |
| `renameOnCopy` | The format string to rename the file on copy. Needs to be compatible with a Java date format pattern. The date used to rename files is the capture date of the photo. |
| `eraseBeforeCopy` | If `true`then the target directory will be erased before copy.                                                                                                        |
| `ejectAfterCopy` | If `true` then the device is ejected after copy. Not supported on the command line.                                                                                   |

With the configuration in place, the application can be executed with:

```
${project.artifactId} <path to source>
```

### macOS application

<img src="imgs/screen1.png" width="600">

The macOS application lives in the menubar and everything can be configured in the menu.

Click on the mounted device to start the copy process.

When the "Shift" key is pressed, and the mounted device or the target directory is selected, then it is opened.

## Use ${project.name} in your application

Add the following dependency:

```
<dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>${project.artifactId}</artifactId>
    <version>${project.version}</version>
</dependency>
```

Add the following repository:

```
<repositories>
    <repository>
        <id>moritz-petersen-maven-repo-releases</id>
        <url>http://maven.moritzpetersen.de/releases</url>
    </repository>
</repositories>
```

## Build and Install ${project.name}

The easiest way to build ${project.name} is to use the makefile:

```
make
```

The makefile will build and compile the application (`make build`) and also install a symlink at `/usr/local/bin/${project.artifactId}` (`make install`).

The `target` directory will also contain a macOS application `target/${project.name}.app`.

You may need to sign the application in order to run properly (especially on Apple Silicon machines). You can use
the `bin/sign.sh` script:

```
bin/sign.sh target/${project.name}.app
```
