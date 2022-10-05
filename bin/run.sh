#!/bin/bash

# Script to run a Java program as command line application
#
# Preconditions:
# 1. The script must be located in a subdirectory of the project directory (e.g. "/bin")
# 2. The executable JAR file is in the "/target" subdirectory of the project and ends with
#    "jar-with-dependencies.jar (as creeated with the Maven Assembly plugin).
# 3. The main class is configured in the manifest file of the JAR file.
# 4. There is only one JAR file which ends with "jar-with-dependencies.jar".
#
# All command line arguments of this script are passed on to the Java program

if [[ -L "$0" ]]; then
  SCRIPT_DIR=$(dirname "$(readlink "$0")")
else
  SCRIPT_DIR=$(dirname "$0")
fi

JAR_FILE=$(ls "$(dirname "$SCRIPT_DIR")"/target/*jar-with-dependencies.jar)

java -jar "$JAR_FILE" "$@"
