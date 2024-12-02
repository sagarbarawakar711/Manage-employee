#!/bin/bash

# Print a message indicating that the script is starting
echo "Starting the Java application..."
java -jar ${CODEBUILD_SRC_DIR}/target/manageemployee-0.0.1
