package core.utils;

import data.exceptions.ConfigurationException;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public final class Downloads {
    private static final Logger log = LoggerFactory.getLogger(Downloads.class);
    private static final String DEFAULT_DIR = "assets/files";

    private Downloads() {
        // utility class - no instances
    }

    public static String directory() {
        String configured = Configuration.get("download.dir");
        if (configured == null || configured.isBlank()) {
            configured = DEFAULT_DIR;
        }
        Path dir = Paths.get(configured).toAbsolutePath().normalize();
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            log.error("Could not create download directory {}", dir, e);
            throw new ConfigurationException("Failed to create download directory " + dir, e);
        }
        return dir.toString();
    }

    public static String file(String name) {
        return Paths.get(directory(), name).toString();
    }

    public static ChromeOptions applyTo(ChromeOptions options) {
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", directory());
        prefs.put("download.prompt_for_download", false);
        prefs.put("download.directory_upgrade", true);
        options.setExperimentalOption("prefs", prefs);
        return options;
    }

    public static EdgeOptions applyTo(EdgeOptions options) {
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", directory());
        prefs.put("download.prompt_for_download", false);
        prefs.put("download.directory_upgrade", true);
        options.setExperimentalOption("prefs", prefs);
        return options;
    }

    public static FirefoxOptions applyTo(FirefoxOptions options) {
        options.addPreference("browser.download.folderList", 2);
        options.addPreference("browser.download.dir", directory());
        options.addPreference("browser.download.useDownloadDir", true);
        options.addPreference("browser.download.manager.showWhenStarting", false);
        return options;
    }
}
