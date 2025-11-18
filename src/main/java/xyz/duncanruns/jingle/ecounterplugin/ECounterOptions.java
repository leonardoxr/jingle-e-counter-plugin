package xyz.duncanruns.jingle.ecounterplugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.Level;
import xyz.duncanruns.jingle.Jingle;
import xyz.duncanruns.jingle.util.FileUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Configuration options for the E-Counter plugin.
 * Handles persistence of capture region, zoom, and FPS settings.
 */
public class ECounterOptions {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path OPTIONS_PATH = Jingle.FOLDER.resolve("ecounter.json");

    // Capture region settings (relative to Minecraft client area)
    public int captureX = 13;
    public int captureY = 37;
    public int captureWidth = 37;
    public int captureHeight = 9;

    // Display settings
    public double zoomFactor = 5.0;
    public int fpsLimit = 30;

    // Window thin mode settings
    public int thinWidth = 400;
    public int thinHeight = 1080;

    /**
     * Load options from disk, or create default if not found.
     */
    public static Optional<ECounterOptions> load() {
        try {
            if (!Files.exists(OPTIONS_PATH)) {
                Jingle.log(Level.INFO, "(E-Counter) No config file found, using defaults");
                ECounterOptions defaults = new ECounterOptions();
                defaults.trySave();
                return Optional.of(defaults);
            }
            ECounterOptions options = FileUtil.readJson(OPTIONS_PATH, ECounterOptions.class);
            return Optional.of(options);
        } catch (IOException e) {
            Jingle.log(Level.ERROR, "(E-Counter) Failed to load options: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Save options to disk.
     */
    public boolean trySave() {
        try {
            FileUtil.writeString(OPTIONS_PATH, GSON.toJson(this));
            Jingle.log(Level.INFO, "(E-Counter) Options saved successfully");
            return true;
        } catch (IOException e) {
            Jingle.log(Level.ERROR, "(E-Counter) Failed to save options: " + e.getMessage());
            return false;
        }
    }
}
