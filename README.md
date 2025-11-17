# Jingle E-Counter Plugin

A Jingle plugin that displays entity counts in a thin, easy-to-read window.

## Features

- **Screen Capture & Zoom**: Captures the F3 entity counter ("E: 0/1") from the top-left of your Minecraft window and zooms it in
- **Thin Window Display**: A compact window similar to Jingle's "Thin BT" feature
- **3x Zoom Factor**: Entity count displayed at 3x size for easy visibility at a glance
- **Crisp Pixel Scaling**: Uses nearest-neighbor interpolation for sharp, readable text
- **Hotkey Toggle**: Show/hide the counter window with a customizable hotkey
- **Real-time Updates**: Screen capture updates every tick while the window is visible
- **Always on Top**: Window stays on top of other applications for easy monitoring

## How It Works

The plugin captures a small region (80x20 pixels) from the top-left corner of your Minecraft window where the F3 debug screen shows "E: 0/1" (entity count). This region is then scaled up 3x and displayed in a small window, making it easy to monitor entity counts without opening the full F3 screen.

## Configuration

You can adjust the capture region and zoom in `ECounterWindow.java`:
- `captureX`, `captureY`: Position relative to Minecraft window (default: 10, 10)
- `captureWidth`, `captureHeight`: Size of capture region (default: 80x20)
- `zoomFactor`: How much to zoom in (default: 3.0)

## Developing

Jingle GUIs are made with the IntelliJ IDEA form designer, if you intend on changing GUI portions of the code, IntelliJ IDEA must be configured in a certain way to ensure the GUI form works properly:
- `Settings` -> `Build, Execution, Deployment` -> `Build Tools` -> `Gradle` -> `Build and run using: IntelliJ Idea`
- `Settings` -> `Editor` -> `GUI Designer` -> `Generate GUI into: Java source code`