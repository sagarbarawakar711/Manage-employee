#!/bin/bash

# Set the path to the Java executable (optional if it's already in your system PATH)
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk
export PATH=$JAVA_HOME/bin:$PATH

# Set the JAR file path (ensure this is the correct path to your .jar file)
JAR_FILE="/opt/tomcat/webapps/manageemployee-0.0.1.jar"

# Check if the JAR file exists
if [ -f "$JAR_FILE" ]; then
    echo "Found JAR file: $JAR_FILE"
    
    # Run the Java application
    java -jar "$JAR_FILE" > /var/log/manageemployee.log 2>&1 &
    echo "Java application started successfully."

else
    echo "JAR file not found: $JAR_FILE"
    exit 1
fi
