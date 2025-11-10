#!/bin/bash

# Set JVM options for better memory management
export MAVEN_OPTS="-Xmx2g -Xms512m -XX:MaxMetaspaceSize=256m"

# Clean and start the application
echo "Starting Digital KYC Application..."
mvn clean spring-boot:run -Dspring-boot.run.profiles=local