package core.utils;

import data.exceptions.ConfigurationException;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Captures browser screenshots, saves them under {@code assets/images} and
 * attaches them to the Allure report. Never throws - capture failures are only
 * logged so a screenshot issue can not break a test.
 */
public final class Screenshot {
    private static final Logger log = LoggerFactory.getLogger(Screenshot.class);
    private static final String DEFAULT_DIR = "assets/images";
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

    private Screenshot() {
        // utility class - no instances
    }

    /**
     * Takes a screenshot of the given driver, saves it as a PNG under
     * {@code assets/images} and attaches it to the current Allure test.
     *
     * @param driver   the active browser driver.
     * @param testName short name used in the file name (e.g. class simple name).
     * @param status   outcome label (e.g. PASS / FAIL).
     * @return the saved file, or {@code null} if the capture failed.
     */
    public static Path capture(WebDriver driver, String testName, String status) {
        if (driver == null) {
            log.warn("Screenshot skipped: no active browser driver");
            return null;
        }
        if (!(driver instanceof TakesScreenshot)) {
            log.warn("Screenshot skipped: driver {} does not support screenshots", driver.getClass().getSimpleName());
            return null;
        }

        try {
            byte[] image = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            String fileName = String.format("%s-%s-%s.png",
                    LocalDateTime.now().format(TIMESTAMP), sanitize(testName), sanitize(status));
            Path file = Paths.get(directory(), fileName);
            Files.write(file, image);
            log.info("Screenshot saved: {}", file);

            try (InputStream stream = Files.newInputStream(file)) {
                Allure.addAttachment("Screenshot (" + status + ")", "image/png", stream, ".png");
            }
            return file;
        } catch (Exception e) {
            log.warn("Screenshot capture failed: {}", e.getMessage());
            return null;
        }
    }

    public static String directory() {
        String configured = Configuration.get("screenshot.dir");
        if (configured == null || configured.isBlank()) {
            configured = DEFAULT_DIR;
        }
        Path dir = Paths.get(configured).toAbsolutePath().normalize();
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            log.error("Could not create screenshot directory {}", dir, e);
            throw new ConfigurationException("Failed to create screenshot directory " + dir, e);
        }
        return dir.toString();
    }

    private static String sanitize(String value) {
        if (value == null) {
            return "unknown";
        }
        return value.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}