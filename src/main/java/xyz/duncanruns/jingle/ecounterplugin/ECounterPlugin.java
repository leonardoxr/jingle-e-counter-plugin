package xyz.duncanruns.jingle.ecounterplugin;

import com.google.common.io.Resources;
import org.apache.logging.log4j.Level;
import xyz.duncanruns.jingle.Jingle;
import xyz.duncanruns.jingle.JingleAppLaunch;
import xyz.duncanruns.jingle.ecounterplugin.gui.ECounterWindow;
import xyz.duncanruns.jingle.instance.MinecraftInstance;
import xyz.duncanruns.jingle.plugin.PluginEvents;
import xyz.duncanruns.jingle.plugin.PluginHotkeys;
import xyz.duncanruns.jingle.plugin.PluginManager;

import java.awt.*;
import java.io.IOException;
import java.nio.charset.Charset;

public class ECounterPlugin {
    private static ECounterWindow counterWindow;
    private static Rectangle originalWindowSize = null;
    private static boolean isActive = false;

    // Thin window settings (adjustable)
    private static final int THIN_WIDTH = 400;  // Width of thin Minecraft window

    public static void main(String[] args) throws IOException {
        // This is only used to test the plugin in the dev environment
        // ECounterPlugin.main itself is never used when users run Jingle

        JingleAppLaunch.launchWithDevPlugin(args, PluginManager.JinglePluginData.fromString(
                Resources.toString(Resources.getResource(ECounterPlugin.class, "/jingle.plugin.json"), Charset.defaultCharset())
        ), ECounterPlugin::initialize);
    }

    public static void initialize() {
        // This gets run once when Jingle launches

        counterWindow = new ECounterWindow();

        // Add hotkey to toggle the counter window and thin mode
        PluginHotkeys.addHotkeyAction("Toggle E-Counter (Thin + Zoom)", () -> {
            toggleECounter();
        });

        // Update entity count from game data
        PluginEvents.END_TICK.register(() -> {
            // This gets run every tick (1 ms)
            // We'll update the counter display here
            if (counterWindow != null && counterWindow.isVisible()) {
                counterWindow.updateFromGameData();
            }
        });

        PluginEvents.STOP.register(() -> {
            // This gets run when Jingle is shutting down
            if (counterWindow != null) {
                counterWindow.dispose();
            }
            Jingle.log(Level.INFO, "E-Counter Plugin shutting down...");
        });

        PluginEvents.RESET_INSTANCE.register(() -> {
            // Restore window size on reset
            if (isActive) {
                restoreMinecraftWindow();
                isActive = false;
            }
        });

        Jingle.log(Level.INFO, "E-Counter Plugin Initialized");
    }

    private static void toggleECounter() {
        MinecraftInstance instance = MinecraftInstance.get();
        if (instance == null) {
            Jingle.log(Level.WARN, "(E-Counter Plugin) No Minecraft instance found");
            return;
        }

        if (isActive) {
            // Disable: restore window and hide counter
            restoreMinecraftWindow();
            counterWindow.setVisible(false);
            isActive = false;
            Jingle.log(Level.INFO, "(E-Counter Plugin) Disabled - window restored");
        } else {
            // Enable: save current size, make thin, show counter
            Rectangle currentWindow = instance.getWindow();
            if (currentWindow != null) {
                originalWindowSize = new Rectangle(currentWindow);

                // Make Minecraft window thin
                int newHeight = currentWindow.height;
                instance.setWindowSize(THIN_WIDTH, newHeight);

                // Position counter window to the right of thin Minecraft window
                counterWindow.setLocation(currentWindow.x + THIN_WIDTH + 5, currentWindow.y);
                counterWindow.setVisible(true);

                isActive = true;
                Jingle.log(Level.INFO, "(E-Counter Plugin) Enabled - thin mode + counter active");
            }
        }
    }

    private static void restoreMinecraftWindow() {
        if (originalWindowSize != null) {
            MinecraftInstance instance = MinecraftInstance.get();
            if (instance != null) {
                instance.setWindowSize(originalWindowSize.width, originalWindowSize.height);
                Jingle.log(Level.INFO, "(E-Counter Plugin) Minecraft window restored");
            }
            originalWindowSize = null;
        }
    }
}
