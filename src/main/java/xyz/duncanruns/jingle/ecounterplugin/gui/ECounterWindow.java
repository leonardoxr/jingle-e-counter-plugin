package xyz.duncanruns.jingle.ecounterplugin.gui;

import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.User32;
import xyz.duncanruns.jingle.Jingle;
import xyz.duncanruns.jingle.instance.OpenedInstance;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * E-Counter overlay window that captures and displays the entity counter region from Minecraft.
 * Uses hardware-accelerated GDI32 StretchBlt for efficient screen capture.
 * Based on the approach from Jingle-EyeSee-Plugin by DuncanRuns.
 */
public class ECounterWindow extends JFrame {
    private JPanel displayPanel;
    private HWND displayHwnd; // Our window's HWND
    private ScheduledExecutorService updateExecutor;
    private boolean debugMode = true;

    // Capture settings - based on waywall configuration
    private int captureX = 13;  // X position relative to Minecraft window client area
    private int captureY = 37;  // Y position relative to Minecraft window client area
    private int captureWidth = 37;  // Width of region to capture (where "E: 0/1" appears)
    private int captureHeight = 9; // Height of region to capture
    private double zoomFactor = 5.0; // How much to zoom in (5x like waywall)
    private int fpsLimit = 30; // FPS limit for capture updates

    public ECounterWindow() {
        super("E-Counter");
        setupWindow();
        startUpdateLoop();
    }

    private void setupWindow() {
        // Calculate window size based on zoom
        int windowWidth = (int) (captureWidth * zoomFactor);
        int windowHeight = (int) (captureHeight * zoomFactor);

        setSize(windowWidth, windowHeight);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setAlwaysOnTop(true);
        setResizable(false);
        setUndecorated(true); // Remove window decorations for cleaner look

        // Position window at top-right corner by default
        setLocationRelativeTo(null);
        Point location = getLocation();
        setLocation(location.x + 400, location.y);

        // Create main panel with dark background
        displayPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // The actual rendering is done via StretchBlt directly to this window
                // This just provides a dark background when no capture is active
            }
        };
        displayPanel.setBackground(new Color(30, 30, 30));
        displayPanel.setPreferredSize(new Dimension(windowWidth, windowHeight));

        add(displayPanel);

        // Don't show by default - user will toggle with hotkey
        setVisible(false);
    }

    /**
     * Start the update loop that periodically captures and displays the counter region.
     */
    private void startUpdateLoop() {
        if (updateExecutor != null && !updateExecutor.isShutdown()) {
            updateExecutor.shutdown();
        }

        updateExecutor = Executors.newSingleThreadScheduledExecutor();
        long delayNanos = 1_000_000_000L / fpsLimit;
        long delayMillis = delayNanos / 1_000_000L;

        updateExecutor.scheduleAtFixedRate(() -> {
            try {
                if (isVisible()) {
                    updateCapture();
                }
            } catch (Exception e) {
                // Silently catch exceptions to prevent executor from stopping
            }
        }, 0, delayMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * Get the HWND for this JFrame.
     */
    private HWND getDisplayHwnd() {
        if (displayHwnd == null) {
            // Get native window handle for this JFrame
            try {
                // Use JNA to get the native window pointer
                com.sun.jna.Pointer pointer = com.sun.jna.Native.getComponentPointer(this);
                if (pointer != null) {
                    displayHwnd = new HWND(pointer);
                }
            } catch (Exception e) {
                Jingle.log(org.apache.logging.log4j.Level.ERROR, "(E-Counter) Failed to get window HWND: " + e.getMessage());
            }
        }
        return displayHwnd;
    }

    /**
     * Capture the entity counter region from Minecraft and display it in this window.
     * Uses hardware-accelerated StretchBlt for efficient screen capture.
     */
    private void updateCapture() {
        // Get the current Minecraft instance
        Optional<OpenedInstance> instanceOpt = Jingle.getMainInstance();
        if (!instanceOpt.isPresent()) {
            return;
        }

        OpenedInstance instance = instanceOpt.get();
        HWND minecraftHwnd = instance.hwnd;

        // Verify the window is valid and visible
        if (!User32.INSTANCE.IsWindow(minecraftHwnd) || !User32.INSTANCE.IsWindowVisible(minecraftHwnd)) {
            if (debugMode) {
                Jingle.log(org.apache.logging.log4j.Level.WARN, "(E-Counter) Minecraft window not valid or not visible");
            }
            return;
        }

        // Get our display window's HWND
        HWND ourHwnd = getDisplayHwnd();
        if (ourHwnd == null) {
            return;
        }

        // Get device contexts
        HDC minecraftDC = User32.INSTANCE.GetDC(minecraftHwnd);
        HDC displayDC = User32.INSTANCE.GetDC(ourHwnd);

        if (minecraftDC == null || displayDC == null) {
            if (minecraftDC != null) User32.INSTANCE.ReleaseDC(minecraftHwnd, minecraftDC);
            if (displayDC != null) User32.INSTANCE.ReleaseDC(ourHwnd, displayDC);
            return;
        }

        try {
            // Debug logging (only once)
            if (debugMode) {
                char[] buffer = new char[512];
                User32.INSTANCE.GetWindowText(minecraftHwnd, buffer, 512);
                String windowTitle = new String(buffer).trim();

                RECT windowRect = new RECT();
                User32.INSTANCE.GetClientRect(minecraftHwnd, windowRect);
                int clientWidth = windowRect.right - windowRect.left;
                int clientHeight = windowRect.bottom - windowRect.top;

                Jingle.log(org.apache.logging.log4j.Level.INFO,
                    String.format("(E-Counter) Capturing from window: '%s' (Client: %dx%d) Region: [%d,%d %dx%d] Zoom: %.1fx",
                        windowTitle, clientWidth, clientHeight, captureX, captureY, captureWidth, captureHeight, zoomFactor));
                debugMode = false;
            }

            // Set stretch mode for better quality
            // COLORONCOLOR (3) is faster and works well for pixel-perfect scaling
            GDI32Extra.INSTANCE.SetStretchBltMode(displayDC, GDI32Extra.COLORONCOLOR);

            // Calculate scaled dimensions
            int scaledWidth = (int) (captureWidth * zoomFactor);
            int scaledHeight = (int) (captureHeight * zoomFactor);

            // Use StretchBlt to copy and scale the region directly from Minecraft window to our window
            // This is hardware-accelerated and much faster than using Robot
            boolean success = GDI32Extra.INSTANCE.StretchBlt(
                displayDC, 0, 0, scaledWidth, scaledHeight,  // Destination: our window at (0,0)
                minecraftDC, captureX, captureY, captureWidth, captureHeight,  // Source: Minecraft client area
                new DWORD(GDI32Extra.SRCCOPY)  // Copy operation
            );

            if (!success && debugMode) {
                Jingle.log(org.apache.logging.log4j.Level.WARN, "(E-Counter) StretchBlt failed");
            }

        } finally {
            // Always release device contexts
            User32.INSTANCE.ReleaseDC(minecraftHwnd, minecraftDC);
            User32.INSTANCE.ReleaseDC(ourHwnd, displayDC);
        }
    }

    /**
     * Legacy method - now just delegates to the automatic update loop.
     */
    public void updateFromGameData() {
        // This method is now handled automatically by the update loop
        // Keeping it for backwards compatibility, but it does nothing
    }

    /**
     * Allow customization of capture region.
     */
    public void setCaptureRegion(int x, int y, int width, int height) {
        this.captureX = x;
        this.captureY = y;
        this.captureWidth = width;
        this.captureHeight = height;

        // Resize window to match new dimensions
        int windowWidth = (int) (width * zoomFactor);
        int windowHeight = (int) (height * zoomFactor);
        setSize(windowWidth, windowHeight);
        displayPanel.setPreferredSize(new Dimension(windowWidth, windowHeight));
        revalidate();
    }

    /**
     * Set the zoom factor for the captured region.
     */
    public void setZoomFactor(double zoom) {
        this.zoomFactor = zoom;

        // Resize window to match new zoom
        int windowWidth = (int) (captureWidth * zoom);
        int windowHeight = (int) (captureHeight * zoom);
        setSize(windowWidth, windowHeight);
        displayPanel.setPreferredSize(new Dimension(windowWidth, windowHeight));
        revalidate();
    }

    /**
     * Set the FPS limit for capture updates.
     */
    public void setFpsLimit(int fps) {
        this.fpsLimit = fps;
        startUpdateLoop(); // Restart with new FPS
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            debugMode = true; // Re-enable debug on show
        }
    }

    @Override
    public void dispose() {
        if (updateExecutor != null) {
            updateExecutor.shutdown();
            try {
                updateExecutor.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                updateExecutor.shutdownNow();
            }
        }
        super.dispose();
    }
}
