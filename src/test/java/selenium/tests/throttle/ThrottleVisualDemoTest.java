package selenium.tests.throttle;

import core.base.DriverFactory;
import core.throttle.CpuProfile;
import core.throttle.NetworkProfile;
import core.throttle.ThrottleManager;
import core.utils.Configuration;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.Map;

/**
 * Visual demo of the network/CPU throttling delay: loads the base page once
 * unthrottled and once throttled, logs the browser's own navigation timing, and
 * saves a screenshot of the throttled load so the difference can be seen.
 */
public class ThrottleVisualDemoTest {

    private static final Logger log = LoggerFactory.getLogger(ThrottleVisualDemoTest.class);
    private static final Duration LOAD_TIMEOUT = Duration.ofSeconds(120);

    private WebDriver driver;
    private JavascriptExecutor js;

    @BeforeMethod
    public void setUp() {
        driver = DriverFactory.create();
        js = (JavascriptExecutor) driver;
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void showThrottlingDelayVisually() throws java.io.IOException {
        String baseUrl = Configuration.get("ui.base.url");

        driver.get(baseUrl + "?throttleDemo=baseline");
        waitForLoad();
        Map<String, Object> baseline = navigationTiming();
        log.info("=========== BASELINE (no throttle) ===========");
        printTiming(baseline);

        ThrottleManager throttle = new ThrottleManager(driver);
        throttle.apply(CpuProfile.X4, NetworkProfile.THREE_G);

        driver.get(baseUrl + "?throttleDemo=throttled&ts=" + System.currentTimeMillis());
        waitForLoad();
        Map<String, Object> throttled = navigationTiming();
        log.info("=========== THROTTLED (X4 CPU + THREE_G) ===========");
        printTiming(throttled);

        long baselineMs = ((Number) baseline.get("duration")).longValue();
        long throttledMs = ((Number) throttled.get("duration")).longValue();
        log.info("=========== DELAY VISIBLE: +{} ms ({}x slower) ===========",
                Math.max(0, throttledMs - baselineMs),
                baselineMs == 0 ? "n/a" : String.format("%.1f", throttledMs / (double) baselineMs));

        File screenshot = capture("throttled-three-g");
        log.info("Screenshot of throttled page saved to: {}", screenshot.getAbsolutePath());
        throttle.reset();
    }

    private void waitForLoad() {
        new WebDriverWait(driver, LOAD_TIMEOUT)
                .until(d -> "complete".equals(
                        ((JavascriptExecutor) d).executeScript("return document.readyState")));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> navigationTiming() {
        return (Map<String, Object>) js.executeScript(
                "var n = performance.getEntriesByType('navigation')[0]; return {"
                        + "duration: n.duration,"
                        + "domContentLoaded: n.domContentLoadedEventEnd,"
                        + "load: n.loadEventEnd,"
                        + "transferSize: n.transferSize,"
                        + "protocol: n.nextHopProtocol};");
    }

    private void printTiming(Map<String, Object> timing) {
        log.info("total navigation: {} ms", ((Number) timing.get("duration")).longValue());
        log.info("DOMContentLoaded: {} ms", ((Number) timing.get("domContentLoaded")).longValue());
        log.info("load event end : {} ms", ((Number) timing.get("load")).longValue());
        log.info("bytes transferred: {} bytes (protocol: {})",
                ((Number) timing.get("transferSize")).longValue(), timing.get("protocol"));
    }

    private File capture(String name) throws java.io.IOException {
        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        Path dir = Paths.get(System.getProperty("user.dir"), "target", "throttle-screenshots");
        Files.createDirectories(dir);
        Path dest = dir.resolve(name + ".png");
        Files.copy(src.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
        return dest.toFile();
    }
}
