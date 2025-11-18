# Jingle E-Counter Plugin

A Jingle plugin that provides a dual-window setup: makes your Minecraft window thin (like "Thin BT") and displays a zoomed, easy-to-read entity counter beside it.

Perfect for speedrunning, monitoring entity counts during gameplay, or any scenario where you need to keep an eye on entities without keeping the full F3 screen open.

## Features

- **Dual-Window Setup**: Makes your Minecraft window thin (like "Thin BT") AND shows a zoomed entity counter beside it
- **Configurable Thin Window**: Customize both width and height of the thin Minecraft window (default: 400x1080)
- **GUI Configuration Panel**: Easy-to-use settings panel in Jingle's plugins tab for all configuration options
- **Persistent Settings**: All settings are automatically saved and restored between sessions
- **Screen Capture & Zoom**: Captures the F3 entity counter ("E: 0/1") with configurable zoom (default: 5x)
- **Hardware-Accelerated Capture**: Uses GDI32 StretchBlt for efficient, high-performance screen capture
- **Adjustable FPS**: Configure capture frame rate from 5-240 FPS (default: 30 FPS) to balance performance
- **Customizable Capture Region**: Fine-tune the exact position and size of the captured area
- **Hotkey Toggle**: Single hotkey enables/disables both thin mode and counter window
- **Auto-Restore**: Minecraft window automatically restored to original size when toggled off or on world exit
- **Always on Top**: Counter window stays on top for easy monitoring

## Installation

1. Download the latest `.jar` file from the [Releases](https://github.com/leonardoxr/jingle-e-counter-plugin/releases) page
2. Place the `.jar` file in your Jingle plugins folder:
   - Windows: `%APPDATA%\.Jingle\plugins\`
   - macOS/Linux: `~/.Jingle/plugins/`
3. Restart Jingle (or reload plugins if available)

## Usage

1. **Configure the plugin** (optional):
   - Open Jingle
   - Go to the "Plugins" tab
   - Find "E-Counter" in the list
   - Adjust settings as needed:
     - **Capture Region**: Position and size of the F3 counter area to capture
     - **Zoom Factor**: How much to zoom in on the captured region (default: 5.0x)
     - **FPS Limit**: How often to update the capture (5-240 FPS, default: 30)
     - **Thin Window Size**: Width and height of Minecraft when thin mode is active
   - Click "Apply Settings" to save

2. **Set up the hotkey**:
   - Go to the "Hotkeys" tab
   - Find "Toggle E-Counter (Thin + Zoom)"
   - Assign your preferred key combination

3. **Enable F3 in Minecraft**:
   - Launch Minecraft and join a world
   - Press F3 to show the debug screen
   - Make sure the entity counter ("E: 0/1") is visible in the top-left

4. **Activate the plugin**:
   - Press your assigned hotkey
   - Your Minecraft window will resize to your configured thin dimensions
   - A zoomed entity counter window will appear (position it wherever you like)
   - The position will be remembered for next time

5. **Deactivate**:
   - Press the hotkey again to restore the original window size and hide the counter
   - The plugin automatically disables when you exit a world

## How It Works

When you press the hotkey:
1. **Minecraft window is resized** to your configured thin dimensions (default: 400x1080)
2. **Counter window appears** where you last positioned it (or at default location)
3. **Entity counter is captured** from the F3 debug screen using hardware-accelerated GDI32 StretchBlt
4. **Image is zoomed** by your configured factor and displayed at your configured FPS
5. **Updates automatically** via a background thread at your configured frame rate

When you toggle off or exit a world, the Minecraft window is restored to its original size and the counter window is hidden.

**Performance**: The plugin uses hardware-accelerated screen capture (GDI32 StretchBlt) which is much more efficient than software-based methods. The configurable FPS limiter lets you balance between smoothness and CPU usage.

## Configuration

All settings can be configured through the Jingle GUI without editing code:

1. Open Jingle and go to the "Plugins" tab
2. Find "E-Counter" in the plugin list
3. Adjust the following settings:

**Capture Region** (relative to Minecraft client area):
- **X**: Horizontal position (default: 13)
- **Y**: Vertical position (default: 37)
- **Width**: Capture width in pixels (default: 37)
- **Height**: Capture height in pixels (default: 9)

**Display Settings**:
- **Zoom Factor**: Magnification level (default: 5.0)
- **FPS Limit**: Update rate from 5-240 FPS (default: 30)

**Thin Window Size**:
- **Width**: Minecraft window width when thin mode is active (default: 400)
- **Height**: Minecraft window height when thin mode is active (default: 1080)

All settings are automatically saved to `~/.Jingle/ecounter.json` and persist between sessions.

**Note**: The default capture region values are based on the standard F3 debug screen layout and match those used in waywall configurations. If your Minecraft uses a different UI scale, you may need to adjust these values.

## Requirements

- [Jingle](https://github.com/DuncanRuns/Jingle) installed and running
- **Windows OS** (uses Windows-specific screen capture APIs)
- Minecraft with F3 debug screen enabled
- Java 8 or higher

## Troubleshooting

**Counter window shows black/wrong content:**
- Make sure F3 is enabled in Minecraft
- Check that the entity counter ("E: 0/1") appears in the top-left of your Minecraft window
- Try adjusting the capture region settings in the E-Counter configuration panel
- If your Minecraft uses a different UI scale, you may need to fine-tune the X/Y position values

**Minecraft window doesn't resize:**
- Ensure you have a Minecraft instance running and a world loaded before toggling the plugin
- Check Jingle logs for any error messages
- Verify your thin window dimensions in the configuration panel are reasonable values

**Counter window position resets:**
- The counter window remembers its position between toggles
- If it appears in an unexpected location, simply move it where you want it
- The new position will be used next time you toggle the plugin

**Performance issues:**
- Try reducing the FPS limit in the configuration panel (default: 30 FPS)
- Lower FPS values use less CPU but update less frequently
- Most users find 20-30 FPS to be a good balance

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

The plugin's GUI is built programmatically using Swing (no form designer files). To develop:

1. Clone the repository
2. Open in your preferred IDE (IntelliJ IDEA, Eclipse, etc.)
3. The main plugin logic is in `ECounterPlugin.java`
4. The configuration panel is in `gui/ECounterPluginPanel.java`
5. The counter window is in `gui/ECounterWindow.java`
6. Run `ECounterPlugin.main()` to test in a development environment

**Note**: Screen capture uses Windows-specific JNA bindings (GDI32 and User32). The plugin will only work on Windows.