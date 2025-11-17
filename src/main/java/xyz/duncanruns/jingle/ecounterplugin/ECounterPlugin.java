package xyz.duncanruns.jingle.ecounterplugin;

import com.google.common.io.Resources;
import org.apache.logging.log4j.Level;
import xyz.duncanruns.jingle.Jingle;
import xyz.duncanruns.jingle.JingleAppLaunch;
import xyz.duncanruns.jingle.ecounterplugin.gui.ECounterWindow;
import xyz.duncanruns.jingle.plugin.PluginEvents;
import xyz.duncanruns.jingle.plugin.PluginHotkeys;
import xyz.duncanruns.jingle.plugin.PluginManager;

import java.io.IOException;
import java.nio.charset.Charset;

public class ECounterPlugin {
    private static ECounterWindow counterWindow;

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

        // Add hotkey to toggle the counter window
        PluginHotkeys.addHotkeyAction("Toggle E-Counter Window", () -> {
            if (counterWindow.isVisible()) {
                counterWindow.setVisible(false);
                Jingle.log(Level.INFO, "(E-Counter Plugin) Window hidden");
            } else {
                counterWindow.setVisible(true);
                Jingle.log(Level.INFO, "(E-Counter Plugin) Window shown");
            }
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

        Jingle.log(Level.INFO, "E-Counter Plugin Initialized");
    }
}
