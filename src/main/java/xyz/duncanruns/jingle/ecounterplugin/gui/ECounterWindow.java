package xyz.duncanruns.jingle.ecounterplugin.gui;

import xyz.duncanruns.jingle.instance.MinecraftInstance;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ECounterWindow extends JFrame {
    private JLabel displayLabel;
    private Robot robot;

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
        MinecraftInstance instance = MinecraftInstance.get();
        if (instance == null) {
            return;
        }

        try {
            // Get Minecraft window position
            Rectangle mcWindow = instance.getWindow();
            if (mcWindow == null) {
                return;
            }

            // Calculate the screen position to capture (top-left of Minecraft window)
            int screenX = mcWindow.x + captureX;
            int screenY = mcWindow.y + captureY;

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
