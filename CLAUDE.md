# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A Jingle plugin for Minecraft speedrunning that provides a dual-window setup: resizes Minecraft to a thin window and displays a zoomed entity counter beside it. The plugin captures the F3 debug screen's entity counter ("E: 0/1") using hardware-accelerated screen capture.

**Platform:** Windows-only (uses Windows-specific JNA bindings for GDI32 and User32 APIs)

## Build Commands

```bash
# Build the plugin
./gradlew build

# Output location
build/libs/jingle-e-counter-plugin-<version>.jar
```

## Development

Run `ECounterPlugin.main()` to test in a development environment with Jingle's dev plugin launcher.

## Architecture

### Core Components

**ECounterPlugin** (`ECounterPlugin.java`)
- Main plugin entry point and lifecycle manager
- `initialize()`: Called once when Jingle launches
  - Loads/creates `ECounterOptions` from disk (`~/.Jingle/ecounter.json`)
  - Creates `ECounterWindow` and applies configuration
  - Registers GUI panel in Jingle's plugin tabs
  - Registers hotkey action for toggling
  - Sets up event handlers (EXIT_WORLD to auto-restore window, STOP for cleanup)
- `toggleECounter()`: Hotkey handler that checks Minecraft window focus and validity before toggling thin mode + counter window
- `applyOptionsToWindow()`: Static method called by GUI panel to apply configuration changes

**ECounterOptions** (`ECounterOptions.java`)
- Configuration data model with JSON persistence via Gson
- Fields: captureX/Y/Width/Height, zoomFactor, fpsLimit, thinWidth/Height
- Default values match waywall configurations for F3 entity counter position
- `load()`: Reads from `~/.Jingle/ecounter.json`, creates defaults if missing
- `trySave()`: Writes current state to disk

**ECounterWindow** (`gui/ECounterWindow.java`)
- JFrame that captures and displays the entity counter region
- Uses `ScheduledExecutorService` for FPS-limited capture loop (configurable 5-240 FPS)
- `updateCapture()`: Core capture logic using hardware-accelerated `GDI32.StretchBlt`
  1. Gets Minecraft window HWND from Jingle's `OpenedInstance`
  2. Gets device contexts for both windows via User32
  3. Uses `StretchBlt` to copy and zoom region directly (much faster than Robot)
  4. Always releases DCs in finally block
- Window is non-focusable (`setFocusableWindowState(false)`) to prevent stealing focus
- Window is always-on-top and undecorated for clean overlay appearance
- Remembers position between toggles

**ECounterPluginPanel** (`gui/ECounterPluginPanel.java`)
- Swing GUI configuration panel (built programmatically, no form designer files)
- Provides text fields for all configuration options with real-time FPS validation (5-240)
- "Apply Settings" button: reads UI values → updates ECounterOptions → saves to disk → calls `ECounterPlugin.applyOptionsToWindow()`
- `onSwitchTo()`: Callback when user switches to this tab, reloads options from disk

**ExtendedUser32/GDI32Extra** (`gui/ExtendedUser32.java`, `gui/GDI32Extra.java`)
- JNA interface extensions for Windows APIs not included in standard JNA platform
- GDI32Extra: Defines `StretchBlt`, `SetStretchBltMode`, and constants (SRCCOPY, COLORONCOLOR)
- ExtendedUser32: Additional User32 functions if needed

### Key Integration Points with Jingle API

- **PluginEvents.EXIT_WORLD**: Automatically restores window size and hides counter when player exits world
- **PluginEvents.STOP**: Cleanup on Jingle shutdown (disposes window and executor service)
- **PluginHotkeys.addHotkeyAction()**: Registers "Toggle E-Counter (Thin + Zoom)" hotkey
- **JingleGUI.addPluginTab()**: Adds configuration panel to Jingle's GUI
- **Resizing.toggleResize()**: Jingle's API for resizing Minecraft window to thin dimensions
- **Resizing.undoResize()**: Restores original window size
- **Jingle.getMainInstance()**: Gets current Minecraft instance with HWND

### Hotkey Behavior

The hotkey only activates when:
1. A Minecraft instance is running with a world loaded
2. The Minecraft window is the foreground window (focused)
3. The Minecraft window is valid and visible

This matches the behavior pattern from Jingle-EyeSee-Plugin to prevent accidental activation.

### Screen Capture Technical Details

Uses Windows GDI32 `StretchBlt` for hardware-accelerated capture:
- Captures directly from Minecraft window's device context (client area coordinates)
- Stretch mode: COLORONCOLOR (fast, good for pixel-perfect scaling)
- No intermediate BufferedImage or Robot (much more efficient)
- FPS-limited via ScheduledExecutorService to balance performance

### Configuration Persistence

All settings stored in `~/.Jingle/ecounter.json` using Gson with pretty printing. File is created with defaults on first run if missing.

## Warning Note

As stated in the README: "This was totally vibe-coded." Code may have unconventional patterns or quick-and-dirty solutions.
