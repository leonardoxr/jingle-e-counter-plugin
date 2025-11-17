# Release v1.0.0 - Jingle E-Counter Plugin

Initial release of the Jingle E-Counter Plugin!

## Features

### Dual-Window Setup
- **Thin Minecraft Window**: Automatically resizes your Minecraft window to 400px width (like "Thin BT")
- **Zoomed Entity Counter**: Displays a 5x magnified view of the F3 entity counter ("E: 0/1") in a separate window
- **Auto-Positioning**: Counter window automatically appears to the right of the thin Minecraft window
- **Smart Restoration**: Window size automatically restored when toggled off or on world reset

### User Experience
- **Single Hotkey Toggle**: One hotkey controls both the thin mode and counter display
- **Real-Time Updates**: Entity count updates every tick while active
- **Crisp Display**: Uses nearest-neighbor interpolation for sharp, readable text
- **Always on Top**: Counter window stays visible over other applications

### Technical Details
- Screen capture coordinates based on standard F3 debug layout (matches waywall config)
- Capture region: 13, 37 with size 37x9 pixels
- Zoom factor: 5x magnification
- Thin window width: 400px (configurable)

## Installation

1. Download `jingle-e-counter-plugin-1.0.0.jar` from this release
2. Place it in your Jingle plugins folder:
   - Windows: `%APPDATA%\.Jingle\plugins\`
   - macOS/Linux: `~/.Jingle/plugins/`
3. Restart Jingle
4. Set up a hotkey for "Toggle E-Counter (Thin + Zoom)" in the Jingle Hotkeys tab

## Requirements

- Jingle installed and running
- Minecraft with F3 debug screen enabled
- Java 8 or higher

## Usage

1. Enable F3 in Minecraft to show the debug screen
2. Press your assigned hotkey to activate
3. Your Minecraft window will become thin and the counter will appear beside it
4. Press the hotkey again to restore everything

## Known Issues

None at this time. Please report any issues on the GitHub issue tracker.

## Credits

- Based on the [Jingle Example Plugin](https://github.com/DuncanRuns/Jingle-Example-Plugin) template
- Inspired by [Jingle-EyeSee-Plugin](https://github.com/DuncanRuns/Jingle-EyeSee-Plugin)
- Screen capture coordinates based on waywall configurations

---

**Full Changelog**: https://github.com/leonardoxr/jingle-e-counter-plugin/commits/v1.0.0
