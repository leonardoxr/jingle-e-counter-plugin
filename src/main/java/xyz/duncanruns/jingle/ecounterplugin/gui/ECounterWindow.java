package xyz.duncanruns.jingle.ecounterplugin.gui;

import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinDef.POINT;
import com.sun.jna.platform.win32.User32;
import xyz.duncanruns.jingle.Jingle;
import xyz.duncanruns.jingle.instance.OpenedInstance;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Optional;

public class ECounterWindow extends JFrame {
    private JLabel displayLabel;
    private Robot robot;
    private boolean debugMode = true; // Enable debug logging initially

    // Capture settings - based on waywall configuration
    private int captureX = 13;  // X position relative to Minecraft window
    private int captureY = 37;  // Y position relative to Minecraft window
    private int captureWidth = 37;  // Width of region to capture (where "E: 0/1" appears)
    private int captureHeight = 9; // Height of region to capture
    private double zoomFactor = 5.0; // How much to zoom in (5x like waywall)

    public ECounterWindow() {
        super("E-Counter");
        setupWindow();
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private void setupWindow() {
        // Calculate window size based on zoom
        int windowWidth = (int) (captureWidth * zoomFactor);
        int windowHeight = (int) (captureHeight * zoomFactor) + 30; // +30 for title

        setSize(windowWidth, windowHeight);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setAlwaysOnTop(true);
        setResizable(false);

        // Position window at top-right corner by default
        setLocationRelativeTo(null);
        Point location = getLocation();
        setLocation(location.x + 400, location.y);

        // Create main panel with dark background
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 30));

        // Create label to display the zoomed screen capture
        displayLabel = new JLabel("", SwingConstants.CENTER);
        displayLabel.setBackground(new Color(30, 30, 30));
        displayLabel.setOpaque(true);

        // Create title label
        JLabel titleLabel = new JLabel("Entity Counter (E:)", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        titleLabel.setForeground(Color.WHITE);

        // Add components to panel
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(displayLabel, BorderLayout.CENTER);

        add(mainPanel);

        // Don't show by default - user will toggle with hotkey
        setVisible(false);
    }

    public void updateFromGameData() {
        if (robot == null) {
            return;
        }

        // Get the current Minecraft instance
        Optional<OpenedInstance> instanceOpt = Jingle.getMainInstance();
        if (!instanceOpt.isPresent()) {
            return;
        }

        OpenedInstance instance = instanceOpt.get();
        HWND hwnd = instance.hwnd;

        try {
            // Verify the window is valid and visible
            if (!User32.INSTANCE.IsWindow(hwnd) || !User32.INSTANCE.IsWindowVisible(hwnd)) {
                if (debugMode) {
                    Jingle.log(org.apache.logging.log4j.Level.WARN, "(E-Counter) Window not valid or not visible");
                }
                return;
            }

            // Get window information for debugging
            if (debugMode) {
                char[] buffer = new char[512];
                User32.INSTANCE.GetWindowText(hwnd, buffer, 512);
                String windowTitle = new String(buffer).trim();

                // Get window dimensions
                RECT clientRect = new RECT();
                User32.INSTANCE.GetClientRect(hwnd, clientRect);
                int clientWidth = clientRect.right - clientRect.left;
                int clientHeight = clientRect.bottom - clientRect.top;

                // Get window position on screen
                RECT windowRect = new RECT();
                User32.INSTANCE.GetWindowRect(hwnd, windowRect);

                // Get top-left corner of client area in screen coordinates
                POINT topLeft = new POINT(0, 0);
                ExtendedUser32.INSTANCE.ClientToScreen(hwnd, topLeft);

                Jingle.log(org.apache.logging.log4j.Level.INFO,
                    String.format("(E-Counter) Window: '%s' | Client size: %dx%d | Window rect: (%d,%d)-(%d,%d) | Client top-left screen pos: (%d,%d)",
                        windowTitle, clientWidth, clientHeight,
                        windowRect.left, windowRect.top, windowRect.right, windowRect.bottom,
                        topLeft.x, topLeft.y));
            }

            // Convert client coordinates to screen coordinates
            // The captureX/captureY are relative to the game's client area (not including title bar)
            POINT clientPoint = new POINT(captureX, captureY);

            if (debugMode) {
                Jingle.log(org.apache.logging.log4j.Level.INFO,
                    String.format("(E-Counter) Client coords: (%d,%d)", clientPoint.x, clientPoint.y));
            }

            ExtendedUser32.INSTANCE.ClientToScreen(hwnd, clientPoint);

            if (debugMode) {
                Jingle.log(org.apache.logging.log4j.Level.INFO,
                    String.format("(E-Counter) Screen coords after conversion: (%d,%d) | Capturing %dx%d region",
                        clientPoint.x, clientPoint.y, captureWidth, captureHeight));
                debugMode = false; // Only log once to avoid spam
            }

            // Verify the converted coordinates are reasonable (on screen)
            if (clientPoint.x < 0 || clientPoint.y < 0) {
                Jingle.log(org.apache.logging.log4j.Level.WARN,
                    "(E-Counter) Invalid screen coordinates: " + clientPoint.x + ", " + clientPoint.y);
                return;
            }

            // Now clientPoint contains the screen coordinates
            int screenX = clientPoint.x;
            int screenY = clientPoint.y;

            // Capture the screen region
            BufferedImage capture = robot.createScreenCapture(
                new Rectangle(screenX, screenY, captureWidth, captureHeight)
            );

            // Scale up the capture (zoom in)
            int scaledWidth = (int) (captureWidth * zoomFactor);
            int scaledHeight = (int) (captureHeight * zoomFactor);

            BufferedImage scaled = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = scaled.createGraphics();

            // Use nearest neighbor for crisp pixel scaling (better for text)
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2d.drawImage(capture, 0, 0, scaledWidth, scaledHeight, null);
            g2d.dispose();

            // Update the display on the EDT
            SwingUtilities.invokeLater(() -> {
                displayLabel.setIcon(new ImageIcon(scaled));
            });

        } catch (Exception e) {
            // Silently ignore errors - might happen if window is minimized, etc.
        }
    }

    // Allow customization of capture region
    public void setCaptureRegion(int x, int y, int width, int height) {
        this.captureX = x;
        this.captureY = y;
        this.captureWidth = width;
        this.captureHeight = height;

        // Resize window to match new dimensions
        int windowWidth = (int) (width * zoomFactor);
        int windowHeight = (int) (height * zoomFactor) + 30;
        setSize(windowWidth, windowHeight);
    }

    public void setZoomFactor(double zoom) {
        this.zoomFactor = zoom;

        // Resize window to match new zoom
        int windowWidth = (int) (captureWidth * zoom);
        int windowHeight = (int) (captureHeight * zoom) + 30;
        setSize(windowWidth, windowHeight);
    }
}
