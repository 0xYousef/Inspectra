package core.utils;

import io.qameta.allure.Allure;
import org.monte.media.Format;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.AWTException;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.monte.media.FormatKeys.EncodingKey;
import static org.monte.media.FormatKeys.FrameRateKey;
import static org.monte.media.FormatKeys.KeyFrameIntervalKey;
import static org.monte.media.FormatKeys.MediaTypeKey;
import static org.monte.media.FormatKeys.MimeTypeKey;
import static org.monte.media.VideoFormatKeys.CompressorNameKey;
import static org.monte.media.VideoFormatKeys.DepthKey;
import static org.monte.media.VideoFormatKeys.ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE;
import static org.monte.media.VideoFormatKeys.QualityKey;

/**
 * Records a browser session with Monte Screen Recorder. Videos are written to
 * {@code assets/videos} and are kept + attached to Allure only when the test
 * fails; on success the recording is deleted. Never throws - failures are only
 * logged so recording can not break a test.
 */
public final class SessionRecorder {
    private static final Logger log = LoggerFactory.getLogger(SessionRecorder.class);
    private static final String DEFAULT_DIR = "assets/videos";
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

    private final Recorder recorder;
    private final String testName;
    private boolean stopped;

    private SessionRecorder(Recorder recorder, String testName) {
        this.recorder = recorder;
        this.testName = testName;
    }

    /**
     * Starts recording the given driver's window (or the full screen as a
     * fallback). Returns {@code null} when recording is unavailable or failed.
     *
     * @param driver   the active browser driver.
     * @param testName short name used in the video file name.
     */
    public static SessionRecorder start(WebDriver driver, String testName) {
        if (GraphicsEnvironment.isHeadless()) {
            log.warn("Video recording skipped: environment is headless");
            return null;
        }
        try {
            Recorder recorder = new Recorder(captureArea(driver), videoDirectory());
            recorder.start();
            return new SessionRecorder(recorder, testName);
        } catch (IOException | AWTException | RuntimeException e) {
            log.warn("Video recording failed to start: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Stops the recording. The video is kept + attached to Allure only when
     * {@code keep} is {@code true}; otherwise the file is deleted.
     */
    public void stop(boolean keep) {
        if (stopped || recorder == null) {
            return;
        }
        stopped = true;
        try {
            recorder.stop();
            File movie = recorder.currentMovieFile();
            if (movie == null || !movie.exists()) {
                log.warn("No video file produced for {}", testName);
                return;
            }
            if (!keep) {
                if (movie.delete()) {
                    log.info("Video discarded for {} (test passed)", testName);
                } else {
                    log.warn("Could not delete video {} for {}", movie, testName);
                }
                return;
            }
            log.info("Video kept: {}", movie.getAbsolutePath());
            try (InputStream stream = Files.newInputStream(movie.toPath())) {
                Allure.addAttachment("Session video (" + testName + ")", "video/x-msvideo", stream, ".avi");
            }
        } catch (IOException | RuntimeException e) {
            log.warn("Video recording failed to stop for {}: {}", testName, e.getMessage());
        }
    }

    private static Rectangle captureArea(WebDriver driver) {
        try {
            Point position = driver.manage().window().getPosition();
            Dimension size = driver.manage().window().getSize();
            if (size != null && size.getWidth() > 0 && size.getHeight() > 0) {
                return new Rectangle(position.getX(), position.getY(), size.getWidth(), size.getHeight());
            }
        } catch (Exception e) {
            log.warn("Could not resolve browser window bounds, recording full screen: {}", e.getMessage());
        }
        return new Rectangle(0, 0,
                Toolkit.getDefaultToolkit().getScreenSize().width,
                Toolkit.getDefaultToolkit().getScreenSize().height);
    }

    private static File videoDirectory() {
        String configured = Configuration.get("video.dir");
        if (configured == null || configured.isBlank()) {
            configured = DEFAULT_DIR;
        }
        Path dir = Paths.get(configured).toAbsolutePath().normalize();
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            log.warn("Could not create video directory {}, recording to tmp", dir);
            return Paths.get(System.getProperty("java.io.tmpdir"), "automation-videos").toFile();
        }
        return dir.toFile();
    }

    /**
     * Monte screen recorder that names each movie file after the test and
     * exposes the current file so it can be kept or deleted.
     */
    private static final class Recorder extends ScreenRecorder {
        private final String movieName;
        private File currentMovie;

        private Recorder(Rectangle captureArea, File movieFolder) throws IOException, AWTException {
            super(GraphicsEnvironment.getLocalGraphicsEnvironment()
                            .getDefaultScreenDevice().getDefaultConfiguration(),
                    captureArea,
                    new Format(org.monte.media.FormatKeys.MediaTypeKey,
                            org.monte.media.FormatKeys.MediaType.FILE,
                            MimeTypeKey, org.monte.media.FormatKeys.MIME_AVI),
                    new Format(MediaTypeKey, org.monte.media.FormatKeys.MediaType.VIDEO,
                            EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                            CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                            DepthKey, 24,
                            FrameRateKey, Rational.valueOf(15),
                            QualityKey, 1.0f,
                            KeyFrameIntervalKey, 15 * 60),
                    new Format(MediaTypeKey, org.monte.media.FormatKeys.MediaType.VIDEO,
                            EncodingKey, "black",
                            FrameRateKey, Rational.valueOf(30)),
                    null,
                    movieFolder);
            this.movieName = LocalDateTime.now().format(TIMESTAMP);
        }

        @Override
        protected File createMovieFile(Format fileFormat) throws IOException {
            if (!movieFolder.exists()) {
                movieFolder.mkdirs();
            }
            currentMovie = new File(movieFolder,
                    movieName + "-" + System.nanoTime() + ".avi");
            return currentMovie;
        }

        private File currentMovieFile() {
            return currentMovie;
        }
    }
}
