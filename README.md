# Jingle E-Counter Plugin

A Jingle plugin that displays entity counts in a thin, easy-to-read window.

## Features

- **Thin Window Display**: A compact, thin window similar to Jingle's "Thin BT" feature
- **Large, Zoomed Text**: Entity count displayed in large 48pt font for easy visibility
- **Color-Coded Feedback**:
  - Green (0-20 entities)
  - Yellow (21-50 entities)
  - Red (50+ entities)
- **Hotkey Toggle**: Show/hide the counter window with a customizable hotkey
- **Real-time Updates**: Entity count updates every tick while the window is visible
- **Always on Top**: Window stays on top of other applications for easy monitoring

## Developing

Jingle GUIs are made with the IntelliJ IDEA form designer, if you intend on changing GUI portions of the code, IntelliJ IDEA must be configured in a certain way to ensure the GUI form works properly:
- `Settings` -> `Build, Execution, Deployment` -> `Build Tools` -> `Gradle` -> `Build and run using: IntelliJ Idea`
- `Settings` -> `Editor` -> `GUI Designer` -> `Generate GUI into: Java source code`