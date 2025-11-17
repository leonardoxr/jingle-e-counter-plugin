package xyz.duncanruns.jingle.ecounterplugin.gui;

import xyz.duncanruns.jingle.instance.MinecraftInstance;

import javax.swing.*;
import java.awt.*;

public class ECounterWindow extends JFrame {
    private JLabel entityCountLabel;
    private int currentEntityCount = 0;

    public ECounterWindow() {
        super("E-Counter");
        setupWindow();
    }

    private void setupWindow() {
        // Create a thin, tall window (similar to "Thin BT" style)
        setSize(120, 200);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setAlwaysOnTop(true);

        // Position window at top-right corner by default
        setLocationRelativeTo(null);
        Point location = getLocation();
        setLocation(location.x + 400, location.y);

        // Create main panel with dark background
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 30));

        // Create label for entity count with large, easy-to-read font
        entityCountLabel = new JLabel("0", SwingConstants.CENTER);
        entityCountLabel.setFont(new Font("Arial", Font.BOLD, 48));
        entityCountLabel.setForeground(new Color(0, 255, 100)); // Bright green for visibility

        // Create title label
        JLabel titleLabel = new JLabel("Entities", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setForeground(Color.WHITE);

        // Add components to panel
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(entityCountLabel, BorderLayout.CENTER);

        add(mainPanel);

        // Don't show by default - user will toggle with hotkey
        setVisible(false);
    }

    public void updateFromGameData() {
        // Get the current Minecraft instance
        MinecraftInstance instance = MinecraftInstance.get();
        if (instance == null) {
            setEntityCount(0);
            return;
        }

        // Try to get entity count from the instance
        try {
            Integer entityCount = instance.getEntities();
            if (entityCount != null) {
                setEntityCount(entityCount);
            } else {
                setEntityCount(0);
            }
        } catch (Exception e) {
            // If we can't get entity count, just keep the current value
        }
    }

    public void setEntityCount(int count) {
        if (this.currentEntityCount != count) {
            this.currentEntityCount = count;
            SwingUtilities.invokeLater(() -> {
                entityCountLabel.setText(String.valueOf(count));

                // Change color based on entity count for visual feedback
                if (count > 50) {
                    entityCountLabel.setForeground(new Color(255, 100, 100)); // Red for high count
                } else if (count > 20) {
                    entityCountLabel.setForeground(new Color(255, 255, 100)); // Yellow for medium count
                } else {
                    entityCountLabel.setForeground(new Color(0, 255, 100)); // Green for low count
                }
            });
        }
    }
}
