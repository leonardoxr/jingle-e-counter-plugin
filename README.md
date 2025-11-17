# Jingle E-Counter Plugin

A Jingle plugin that displays entity counts in a thin, easy-to-read window.

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

## Developing

Jingle GUIs are made with the IntelliJ IDEA form designer, if you intend on changing GUI portions of the code, IntelliJ IDEA must be configured in a certain way to ensure the GUI form works properly:
- `Settings` -> `Build, Execution, Deployment` -> `Build Tools` -> `Gradle` -> `Build and run using: IntelliJ Idea`
- `Settings` -> `Editor` -> `GUI Designer` -> `Generate GUI into: Java source code`