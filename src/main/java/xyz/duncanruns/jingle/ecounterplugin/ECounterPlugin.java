package xyz.duncanruns.jingle.ecounterplugin;

import com.google.common.io.Resources;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import org.apache.logging.log4j.Level;
import xyz.duncanruns.jingle.Jingle;
import xyz.duncanruns.jingle.JingleAppLaunch;
import xyz.duncanruns.jingle.ecounterplugin.gui.ECounterPluginPanel;
import xyz.duncanruns.jingle.ecounterplugin.gui.ECounterWindow;
import xyz.duncanruns.jingle.gui.JingleGUI;
import xyz.duncanruns.jingle.instance.OpenedInstance;
import xyz.duncanruns.jingle.plugin.PluginEvents;
import xyz.duncanruns.jingle.plugin.PluginHotkeys;
import xyz.duncanruns.jingle.plugin.PluginManager;
import xyz.duncanruns.jingle.resizing.Resizing;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

public class ECounterPlugin {
    private static ECounterWindow counterWindow;
    private static ECounterOptions options;
    private static boolean isActive = false;

    public static void main(String[] args) throws IOException {
        // This is only used to test the plugin in the dev environment
        // ECounterPlugin.main itself is never used when users run Jingle

        JingleAppLaunch.launchWithDevPlugin(args, PluginManager.JinglePluginData.fromString(
                Resources.toString(Resources.getResource(ECounterPlugin.class, "/jingle.plugin.json"), Charset.defaultCharset())
        ), ECounterPlugin::initialize);
    }

    public static void initialize() {
        // This gets run once when Jingle launches

        // Load options from disk (or create defaults)
        options = ECounterOptions.load().orElse(new ECounterOptions());

        // Create counter window with loaded settings
        counterWindow = new ECounterWindow();
        applyOptionsToWindow();

        // Create and register plugin panel in Jingle GUI
        ECounterPluginPanel pluginPanel = new ECounterPluginPanel(options);
        JingleGUI.addPluginTab("E-Counter", pluginPanel.mainPanel, pluginPanel::onSwitchTo);

        // Add hotkey to toggle the counter window and thin mode
        PluginHotkeys.addHotkeyAction("Toggle E-Counter (Thin + Zoom)", () -> {
            toggleECounter();
        });

        // The counter window now updates automatically via its internal ScheduledExecutorService
        // No need for END_TICK event handler anymore

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

    /**
     * Apply current options to the counter window.
     * Called from the plugin panel when settings are changed.
     */
    public static void applyOptionsToWindow() {
        if (counterWindow != null && options != null) {
            counterWindow.setCaptureRegion(options.captureX, options.captureY,
                                          options.captureWidth, options.captureHeight);
            counterWindow.setZoomFactor(options.zoomFactor);
            counterWindow.setFpsLimit(options.fpsLimit);
            Jingle.log(Level.INFO, "(E-Counter) Applied options to window");
        }
    }

    private static void toggleECounter() {
        Optional<OpenedInstance> instanceOpt = Jingle.getMainInstance();
        if (!instanceOpt.isPresent()) {
            Jingle.log(Level.WARN, "(E-Counter Plugin) No Minecraft instance found");
            return;
        }

        OpenedInstance instance = instanceOpt.get();
        HWND minecraftHwnd = instance.hwnd;

        // Check if Minecraft window is focused (foreground window)
        HWND foregroundWindow = User32.INSTANCE.GetForegroundWindow();
        if (foregroundWindow == null || !foregroundWindow.equals(minecraftHwnd)) {
            Jingle.log(Level.DEBUG, "(E-Counter Plugin) Hotkey ignored - Minecraft window not focused");
            return;
        }

        // Check if Minecraft window is visible and valid
        if (!User32.INSTANCE.IsWindow(minecraftHwnd) || !User32.INSTANCE.IsWindowVisible(minecraftHwnd)) {
            Jingle.log(Level.WARN, "(E-Counter Plugin) Minecraft window not valid or not visible");
            return;
        }

        if (isActive) {
            // Disable: restore window and hide counter
            Resizing.undoResize();
            counterWindow.setVisible(false);
            isActive = false;
            Jingle.log(Level.INFO, "(E-Counter Plugin) Disabled - window restored");
        } else {
            // Enable: make thin and show counter using configured dimensions
            boolean resized = Resizing.toggleResize(options.thinWidth, options.thinHeight);
            if (resized) {
                // Show counter window (user can position it manually)
                counterWindow.setVisible(true);
                isActive = true;
                Jingle.log(Level.INFO, String.format("(E-Counter Plugin) Enabled - thin mode (%dx%d) + counter active",
                        options.thinWidth, options.thinHeight));
            } else {
                Jingle.log(Level.WARN, "(E-Counter Plugin) Failed to resize window");
            }
        }
    }
}
