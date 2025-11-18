package xyz.duncanruns.jingle.ecounterplugin.gui;

import org.apache.logging.log4j.Level;
import xyz.duncanruns.jingle.Jingle;
import xyz.duncanruns.jingle.ecounterplugin.ECounterOptions;
import xyz.duncanruns.jingle.ecounterplugin.ECounterPlugin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * Configuration panel for the E-Counter plugin.
 * Provides UI controls for adjusting capture region, zoom, and FPS settings.
 */
public class ECounterPluginPanel extends JPanel {
    public final JPanel mainPanel;
    private final ECounterOptions options;

    // Capture region controls
    private JTextField captureXField;
    private JTextField captureYField;
    private JTextField captureWidthField;
    private JTextField captureHeightField;

    // Display controls
    private JTextField zoomFactorField;
    private JTextField fpsLimitField;

    // Thin window controls
    private JTextField thinWidthField;
    private JTextField thinHeightField;

    // Apply button
    private JButton applyButton;

    public ECounterPluginPanel(ECounterOptions options) {
        this.options = options;
        this.mainPanel = new JPanel();
        setupUI();
        loadOptionsToUI();
    }

    private void setupUI() {
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("E-Counter Configuration");
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(15));

        // Capture Region Section
        mainPanel.add(createSectionLabel("Capture Region (relative to Minecraft window)"));
        mainPanel.add(Box.createVerticalStrut(5));

        JPanel capturePanel = new JPanel(new GridLayout(2, 4, 10, 5));
        capturePanel.setMaximumSize(new Dimension(600, 80));
        capturePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        captureXField = createNumberField(options.captureX);
        captureYField = createNumberField(options.captureY);
        captureWidthField = createNumberField(options.captureWidth);
        captureHeightField = createNumberField(options.captureHeight);

        capturePanel.add(new JLabel("X:"));
        capturePanel.add(new JLabel("Y:"));
        capturePanel.add(new JLabel("Width:"));
        capturePanel.add(new JLabel("Height:"));

        capturePanel.add(captureXField);
        capturePanel.add(captureYField);
        capturePanel.add(captureWidthField);
        capturePanel.add(captureHeightField);

        mainPanel.add(capturePanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // Display Settings Section
        mainPanel.add(createSectionLabel("Display Settings"));
        mainPanel.add(Box.createVerticalStrut(5));

        JPanel displayPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        displayPanel.setMaximumSize(new Dimension(300, 80));
        displayPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        zoomFactorField = createNumberField(options.zoomFactor);
        fpsLimitField = createNumberField(options.fpsLimit);

        // Add real-time FPS validation
        fpsLimitField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { validateFPS(); }
            @Override
            public void removeUpdate(DocumentEvent e) { validateFPS(); }
            @Override
            public void changedUpdate(DocumentEvent e) { validateFPS(); }

            private void validateFPS() {
                SwingUtilities.invokeLater(() -> {
                    try {
                        int fps = getIntFromField(fpsLimitField, options.fpsLimit);
                        int clamped = clamp(fps, 5, 240);
                        if (fps != clamped) {
                            fpsLimitField.setText(String.valueOf(clamped));
                        }
                    } catch (Exception ignored) {}
                });
            }
        });

        displayPanel.add(new JLabel("Zoom Factor:"));
        displayPanel.add(new JLabel("FPS Limit (5-240):"));
        displayPanel.add(zoomFactorField);
        displayPanel.add(fpsLimitField);

        mainPanel.add(displayPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // Thin Window Settings Section
        mainPanel.add(createSectionLabel("Thin Window Size (when enabled)"));
        mainPanel.add(Box.createVerticalStrut(5));

        JPanel thinPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        thinPanel.setMaximumSize(new Dimension(300, 80));
        thinPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        thinWidthField = createNumberField(options.thinWidth);
        thinHeightField = createNumberField(options.thinHeight);

        thinPanel.add(new JLabel("Width:"));
        thinPanel.add(new JLabel("Height:"));
        thinPanel.add(thinWidthField);
        thinPanel.add(thinHeightField);

        mainPanel.add(thinPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Apply Button
        applyButton = new JButton("Apply Settings");
        applyButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        applyButton.addActionListener(e -> applySettings());
        mainPanel.add(applyButton);

        mainPanel.add(Box.createVerticalStrut(15));

        // Info label
        JLabel infoLabel = new JLabel("<html><i>Note: Changes are applied immediately when you click 'Apply Settings'.<br>" +
                "Toggle the E-Counter with the configured hotkey to see changes.</i></html>");
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoLabel.setForeground(Color.GRAY);
        mainPanel.add(infoLabel);

        // Add glue to push everything to the top
        mainPanel.add(Box.createVerticalGlue());
    }

    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(label.getFont().getName(), Font.BOLD, 12));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField createNumberField(Number value) {
        JTextField field = new JTextField(value.toString());
        field.setMaximumSize(new Dimension(100, 25));
        return field;
    }

    private void loadOptionsToUI() {
        captureXField.setText(String.valueOf(options.captureX));
        captureYField.setText(String.valueOf(options.captureY));
        captureWidthField.setText(String.valueOf(options.captureWidth));
        captureHeightField.setText(String.valueOf(options.captureHeight));
        zoomFactorField.setText(String.valueOf(options.zoomFactor));
        fpsLimitField.setText(String.valueOf(options.fpsLimit));
        thinWidthField.setText(String.valueOf(options.thinWidth));
        thinHeightField.setText(String.valueOf(options.thinHeight));
    }

    private void applySettings() {
        try {
            // Read values from UI
            options.captureX = getIntFromField(captureXField, options.captureX);
            options.captureY = getIntFromField(captureYField, options.captureY);
            options.captureWidth = getIntFromField(captureWidthField, options.captureWidth);
            options.captureHeight = getIntFromField(captureHeightField, options.captureHeight);
            options.zoomFactor = getDoubleFromField(zoomFactorField, options.zoomFactor);
            options.fpsLimit = clamp(getIntFromField(fpsLimitField, options.fpsLimit), 5, 240);
            options.thinWidth = getIntFromField(thinWidthField, options.thinWidth);
            options.thinHeight = getIntFromField(thinHeightField, options.thinHeight);

            // Save to disk
            if (options.trySave()) {
                // Apply to the active window if it exists
                ECounterPlugin.applyOptionsToWindow();

                JOptionPane.showMessageDialog(mainPanel,
                        "Settings applied successfully!\nToggle E-Counter to see changes.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                Jingle.log(Level.INFO, "(E-Counter) Settings applied from UI");
            } else {
                JOptionPane.showMessageDialog(mainPanel,
                        "Failed to save settings to disk.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainPanel,
                    "Invalid input: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            Jingle.log(Level.ERROR, "(E-Counter) Failed to apply settings: " + e.getMessage());
        }
    }

    private int getIntFromField(JTextField field, int defaultValue) {
        try {
            String text = field.getText().trim();
            if (text.isEmpty()) return defaultValue;
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private double getDoubleFromField(JTextField field, double defaultValue) {
        try {
            String text = field.getText().trim();
            if (text.isEmpty()) return defaultValue;
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Called when the user switches to this tab.
     * Reload options from disk in case they were changed externally.
     */
    public void onSwitchTo() {
        ECounterOptions.load().ifPresent(loadedOptions -> {
            // Update the reference in the plugin
            loadOptionsToUI();
        });
    }
}
