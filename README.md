# Photocopy

## Installation

Add the following dependency:

```
<dependency>
    <groupId>de.moritzpetersen</groupId>
    <artifactId>photocopy</artifactId>
    <version>1.0-SNAPSHOT</version>
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

## Signing the application

You may need to sign the application in order to run properly (especially on Apple Silicon machines). You can use
the `bin/sign.sh` script:

```
bin/sign.sh <path-to-app>
```

For example:

```
bin/sign.sh target/Photocopy.app
```