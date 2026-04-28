#!/bin/bash

# Build script for belief-revision
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JAR_FILE="$PROJECT_DIR/target/belief-revision-1.0-SNAPSHOT.jar"

# Build the project if the JAR doesn't exist
if [ ! -f "$JAR_FILE" ]; then
    echo "Building project..."
    cd "$PROJECT_DIR"
    mvn package -DskipTests -q
    if [ $? -ne 0 ]; then
        echo "Build failed"
        exit 1
    fi
fi

# Run the JAR with the provided arguments
java -jar "$JAR_FILE" "$@"

