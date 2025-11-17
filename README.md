# Jingle E-Counter Plugin

A Jingle plugin that provides a dual-window setup: makes your Minecraft window thin (like "Thin BT") and displays a zoomed, easy-to-read entity counter beside it.

Perfect for speedrunning, monitoring entity counts during gameplay, or any scenario where you need to keep an eye on entities without keeping the full F3 screen open.

## Features

- **Dual-Window Setup**: Makes your Minecraft window thin (like "Thin BT") AND shows a zoomed entity counter beside it
- **Thin Minecraft Window**: Resizes Minecraft to 400px width for a narrow view
- **Screen Capture & Zoom**: Captures the F3 entity counter ("E: 0/1") and zooms it 5x
- **Auto-Positioning**: Counter window automatically appears beside the thin Minecraft window
- **Crisp Pixel Scaling**: Uses nearest-neighbor interpolation for sharp, readable text
- **Hotkey Toggle**: Single hotkey enables/disables both thin mode and counter window
- **Auto-Restore**: Minecraft window automatically restored to original size when toggled off or on reset
- **Real-time Updates**: Screen capture updates every tick while active
- **Always on Top**: Counter window stays on top for easy monitoring

## Installation

1. Download the latest `.jar` file from the [Releases](https://github.com/leonardoxr/jingle-e-counter-plugin/releases) page
2. Place the `.jar` file in your Jingle plugins folder:
   - Windows: `%APPDATA%\.Jingle\plugins\`
   - macOS/Linux: `~/.Jingle/plugins/`
3. Restart Jingle (or reload plugins if available)

## Usage

1. **Set up the hotkey**:
   - Open Jingle
   - Go to the Hotkeys tab
   - Find "Toggle E-Counter (Thin + Zoom)"
   - Assign your preferred key combination

2. **Enable F3 in Minecraft**:
   - Press F3 to show the debug screen
   - Make sure the entity counter ("E: 0/1") is visible in the top-left

3. **Activate the plugin**:
   - Press your assigned hotkey
   - Your Minecraft window will become thin (400px width)
   - A zoomed entity counter window will appear to the right

4. **Deactivate**:
   - Press the hotkey again to restore the original window size and hide the counter

## How It Works

When you press the hotkey:
1. **Minecraft window is resized** to 400px width (thin mode, like "Thin BT")
2. **Counter window appears** to the right of the thin Minecraft window
3. **Entity counter is captured** from the top-left of Minecraft where F3 shows "E: 0/1"
4. **Image is zoomed** 5x and displayed in real-time

When you toggle off, the Minecraft window is restored to its original size and the counter window is hidden.

## Configuration

You can adjust the settings in the code:

**In `ECounterPlugin.java`:**
- `THIN_WIDTH`: Width of thin Minecraft window (default: 400px)

**In `ECounterWindow.java`:**
- `captureX`, `captureY`: Position relative to Minecraft window (default: 13, 37)
- `captureWidth`, `captureHeight`: Size of capture region (default: 37x9)
- `zoomFactor`: How much to zoom in (default: 5.0)

These values are based on the standard F3 debug screen layout and match those used in waywall configurations.

## Requirements

- [Jingle](https://github.com/DuncanRuns/Jingle) installed and running
- Minecraft with F3 debug screen enabled
- Java 8 or higher

## Troubleshooting

**Counter window shows black/wrong content:**
- Make sure F3 is enabled in Minecraft
- Check that the entity counter ("E: 0/1") appears in the top-left of your Minecraft window
- Try adjusting `captureX` and `captureY` values if your Minecraft has a different UI scale

**Minecraft window doesn't resize:**
- Ensure you have a Minecraft instance running before toggling the plugin
- Check Jingle logs for any error messages

**Counter window appears in wrong position:**
- The plugin positions the counter window relative to the thin Minecraft window
- You can manually move it, but it will reset on next toggle

## Building from Source

```bash
git clone https://github.com/leonardoxr/jingle-e-counter-plugin.git
cd jingle-e-counter-plugin
./gradlew build
```

The compiled `.jar` will be in `build/libs/`

## Credits

- Based on the [Jingle Example Plugin](https://github.com/DuncanRuns/Jingle-Example-Plugin) template
- Inspired by [Jingle-EyeSee-Plugin](https://github.com/DuncanRuns/Jingle-EyeSee-Plugin)
- Screen capture coordinates based on waywall configurations

## License

See [LICENSE](LICENSE) file for details.

## Developing

Jingle GUIs are made with the IntelliJ IDEA form designer, if you intend on changing GUI portions of the code, IntelliJ IDEA must be configured in a certain way to ensure the GUI form works properly:
- `Settings` -> `Build, Execution, Deployment` -> `Build Tools` -> `Gradle` -> `Build and run using: IntelliJ Idea`
- `Settings` -> `Editor` -> `GUI Designer` -> `Generate GUI into: Java source code`