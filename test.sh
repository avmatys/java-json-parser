#!/bin/bash
source ./env.sh
set -e

if [ ! -d "$OUT_DIR" ] || [ -z "$(ls -A $OUT_DIR)" ]; then
    echo "Main code is not build. Running build.sh..."
    ./build.sh
fi

echo "-- Compile tests --"
javac -d "$OUT_DIR" -cp "$JUNIT_JAR${OS_SEP}$OUT_DIR" $(find "$TST_DIR" -name "*.java")

echo "-- Run JUnit 6 --"
java -jar "$JUNIT_JAR" execute --classpath "$OUT_DIR" --scan-classpath
