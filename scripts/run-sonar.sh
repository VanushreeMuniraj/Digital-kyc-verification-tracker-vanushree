#!/bin/bash

# Run SonarQube analysis locally
echo "Running SonarQube analysis..."

# First run tests and generate coverage reports
mvn clean test jacoco:report

# Check if local SonarQube is running
if curl -s http://localhost:9000 > /dev/null; then
    echo "Running local SonarQube analysis..."
    mvn sonar:sonar
else
    echo "Local SonarQube server not running at http://localhost:9000"
    echo "To start SonarQube locally:"
    echo "1. Download SonarQube from https://www.sonarqube.org/downloads/"
    echo "2. Extract and run: bin/[OS]/sonar.sh start"
    echo "3. Access http://localhost:9000 (admin/admin)"
fi

echo "Analysis complete!"