#!/bin/bash
source ./env.sh
set -e

echo "-- Clean $OUT_DIR --"
rm -rf "$OUT_DIR"
mkdir "$OUT_DIR"

echo "-- Compile from $SRC_DIR --"
javac -d "$OUT_DIR" $(find "$SRC_DIR" -name "*.java")

echo "-- Source code is compiled --"

