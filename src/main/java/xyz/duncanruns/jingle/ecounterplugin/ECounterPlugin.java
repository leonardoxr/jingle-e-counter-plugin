package xyz.duncanruns.jingle.ecounterplugin;

import com.google.common.io.Resources;
import org.apache.logging.log4j.Level;
import xyz.duncanruns.jingle.Jingle;
import xyz.duncanruns.jingle.JingleAppLaunch;
import xyz.duncanruns.jingle.ecounterplugin.gui.ECounterWindow;
import xyz.duncanruns.jingle.plugin.PluginEvents;
import xyz.duncanruns.jingle.plugin.PluginHotkeys;
import xyz.duncanruns.jingle.plugin.PluginManager;
import xyz.duncanruns.jingle.resizing.Resizing;

import java.io.IOException;
import java.nio.charset.Charset;

public class ECounterPlugin {
    private static ECounterWindow counterWindow;
    private static boolean isActive = false;

    // Thin window settings (adjustable)
    private static final int THIN_WIDTH = 400;  // Width of thin Minecraft window
    private static final int THIN_HEIGHT = 1080;  // Height of thin Minecraft window

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

        PluginEvents.EXIT_WORLD.register(() -> {
            // Restore window size on world exit
            if (isActive) {
                Resizing.undoResize();
                counterWindow.setVisible(false);
                isActive = false;
                Jingle.log(Level.INFO, "(E-Counter Plugin) Auto-disabled on world exit");
            }
        });

        Jingle.log(Level.INFO, "E-Counter Plugin Initialized");
    }

    private static void toggleECounter() {
        if (!Jingle.getMainInstance().isPresent()) {
            Jingle.log(Level.WARN, "(E-Counter Plugin) No Minecraft instance found");
            return;
        }

        if (isActive) {
            // Disable: restore window and hide counter
            Resizing.undoResize();
            counterWindow.setVisible(false);
            isActive = false;
            Jingle.log(Level.INFO, "(E-Counter Plugin) Disabled - window restored");
        } else {
            // Enable: make thin and show counter
            boolean resized = Resizing.toggleResize(THIN_WIDTH, THIN_HEIGHT);
            if (resized) {
                // Show counter window (user can position it manually)
                counterWindow.setVisible(true);
                isActive = true;
                Jingle.log(Level.INFO, "(E-Counter Plugin) Enabled - thin mode + counter active");
            } else {
                Jingle.log(Level.WARN, "(E-Counter Plugin) Failed to resize window");
            }
        }
    }
}
