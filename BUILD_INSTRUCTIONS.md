# Build Instructions for Jingle E-Counter Plugin

## Building the .jar file

Since the plugin requires the Jingle dependency from JitPack, you need to build it on a machine with internet access.

### Prerequisites

- Java 8 or higher
- Internet connection (to download dependencies)
- Git (optional, for cloning)

### Steps

1. **Navigate to the project directory**:
   ```bash
   cd jingle-e-counter-plugin
   ```

2. **Build using Gradle wrapper**:
   ```bash
   # On Linux/Mac:
   ./gradlew clean build

   # On Windows:
   gradlew.bat clean build
   ```

3. **Find the compiled .jar**:
   - The .jar file will be in: `build/libs/`
   - Filename: `jingle-e-counter-plugin-1.0.0.jar`

### Alternative: Use system Gradle

If you have Gradle installed system-wide:
```bash
gradle clean build
```

### Troubleshooting

**Build fails with dependency errors:**
- Make sure you have internet access
- Try running `./gradlew clean --refresh-dependencies build`
- Check that JitPack is accessible: https://jitpack.io

**Java version issues:**
- Ensure you're using Java 8 or higher
- Check version: `java -version`
- Set JAVA_HOME if needed

**Gradle wrapper issues:**
- Make sure `gradlew` is executable: `chmod +x gradlew`
- Delete `.gradle` folder and try again

## Creating a GitHub Release

After building the .jar:

1. Go to https://github.com/leonardoxr/jingle-e-counter-plugin/releases/new
2. Tag version: `v1.0.0`
3. Release title: `v1.0.0 - Initial Release`
4. Copy the content from `RELEASE_NOTES.md` into the description
5. Upload the .jar file: `build/libs/jingle-e-counter-plugin-1.0.0.jar`
6. Click "Publish release"

## Quick Release Script

Here's a complete script to build and prepare for release:

```bash
#!/bin/bash

# Build the plugin
./gradlew clean build

# Check if build succeeded
if [ $? -eq 0 ]; then
    echo "Build successful!"
    echo "JAR location: build/libs/jingle-e-counter-plugin-1.0.0.jar"
    echo ""
    echo "Next steps:"
    echo "1. Go to: https://github.com/leonardoxr/jingle-e-counter-plugin/releases/new"
    echo "2. Create release v1.0.0"
    echo "3. Upload: build/libs/jingle-e-counter-plugin-1.0.0.jar"
    echo "4. Use content from RELEASE_NOTES.md for the description"
else
    echo "Build failed! Check the error messages above."
    exit 1
fi
```

Save this as `build-release.sh`, make it executable (`chmod +x build-release.sh`), and run it.
