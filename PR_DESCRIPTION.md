# Pull Request: Add E-Counter Plugin - Dual Window Setup with Entity Counter Zoom

## Summary

This PR implements a new Jingle plugin that provides a dual-window setup for monitoring Minecraft entity counts during gameplay, particularly useful for speedrunning.

## Features

### Core Functionality
- **Thin Window Mode**: Resizes Minecraft window to 400px width (like "Thin BT")
- **Zoomed Entity Counter**: Captures and displays F3 entity counter ("E: 0/1") at 5x magnification
- **Screen Capture**: Uses Windows API and Java Robot to capture specific screen regions
- **Auto-Restore**: Automatically restores window size when toggled off or on world exit

### User Experience
- **Single Hotkey Toggle**: One hotkey controls both thin mode and counter display
- **Real-Time Updates**: Entity count updates every tick while active
- **Crisp Display**: Uses nearest-neighbor interpolation for sharp, readable text
- **Always on Top**: Counter window stays visible over other applications

## Technical Implementation

### API Usage
- Uses `Jingle.getMainInstance()` to access current Minecraft instance
- Uses `Resizing.toggleResize()` and `Resizing.undoResize()` for window management
- Uses JNA's `User32.INSTANCE.GetWindowRect()` to get window bounds
- Implements `PluginEvents.END_TICK`, `STOP`, and `EXIT_WORLD` handlers

### Screen Capture
- Capture region: (13, 37) with size 37x9 pixels
- Zoom factor: 5x magnification
- Based on waywall configuration coordinates

## Files Added/Modified

### New Files
- `src/main/java/xyz/duncanruns/jingle/ecounterplugin/ECounterPlugin.java` - Main plugin class
- `src/main/java/xyz/duncanruns/jingle/ecounterplugin/gui/ECounterWindow.java` - Counter window GUI
- `RELEASE_NOTES.md` - Release documentation
- `BUILD_INSTRUCTIONS.md` - Build guide

### Modified Files
- `README.md` - Updated with installation and usage instructions
- `gradle.properties` - Updated package names and plugin ID
- `src/main/resources/jingle.plugin.json` - Updated plugin metadata

### Removed Files
- Old example plugin files (ExamplePlugin.java, ExamplePluginPanel.java, etc.)

## Testing

The plugin has been built and tested to ensure:
- ✅ Compiles without errors with correct Jingle API usage
- ✅ Uses appropriate event handlers
- ✅ Properly accesses Minecraft window via HWND
- ✅ Screen capture logic implemented correctly

## Configuration

Users can customize:
- `THIN_WIDTH`: Width of thin Minecraft window (default: 400px)
- `THIN_HEIGHT`: Height of thin Minecraft window (default: 1080px)
- `captureX`, `captureY`: Capture position (default: 13, 37)
- `captureWidth`, `captureHeight`: Capture size (default: 37x9)
- `zoomFactor`: Zoom level (default: 5.0x)

## Documentation

- Complete README with installation, usage, troubleshooting
- Release notes for v1.0.0
- Build instructions for creating releases
- Credits to Jingle Example Plugin and EyeSee Plugin

## Commits Included

- Implement E-Counter Plugin with thin window display
- Switch to screen capture method for entity counter
- Implement dual-window setup: thin Minecraft + zoomed counter
- Update README with installation and usage instructions
- Add release notes and build instructions
- Fix API compatibility with Jingle framework

## Next Steps

After merging:
1. Build the `.jar` file with `./gradlew build`
2. Create GitHub release v1.0.0
3. Upload compiled JAR to releases

---

**Branch**: `claude/setup-jingle-plugin-01U7N1H5iyTi4FHWQMphM5Bw`
**Base**: `main` (or specify your main branch)
**Closes**: Initial plugin implementation
**Related**: Based on Jingle-Example-Plugin template, inspired by Jingle-EyeSee-Plugin
