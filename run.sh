#!/bin/bash

# Path to the native executable
BINARY="build/native/nativeCompile/k"

# Check if binary exists
if [ ! -f "$BINARY" ]; then
    echo "Binary not found, running nativeCompile..."
    ./gradlew nativeCompile
fi

# Execute the binary with all passed arguments
if [ -f "$BINARY" ]; then
    exec "./$BINARY" "$@"
else
    echo "Error: Native compilation failed or binary not found at $BINARY"
    exit 1
fi
