package core.base;

import core.throttle.ConfigManager;
import core.throttle.ThrottleManager;
import core.utils.Downloads;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.HasDevTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DriverFactory {

    private static final Logger log = LoggerFactory.getLogger(DriverFactory.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    public static WebDriver setup(DRIVERS driverType, DEVICES device) {
        WebDriver newDriver = newDriver(driverType);
        newDriver.manage().window().setSize(device.size());
        applyConfiguredThrottling(newDriver);
        driverThreadLocal.set(newDriver);
        return newDriver;
    }

    public static WebDriver create() {
        WebDriver newDriver = new ChromeDriver(Downloads.applyTo(new ChromeOptions()));
        applyConfiguredThrottling(newDriver);
        return newDriver;
    }

    private static WebDriver newDriver(DRIVERS driverType) {
        try {
            return driverType.getDriver();
        } catch (RuntimeException e) {
            log.warn("Failed to initialize {} driver ({}). Falling back to CHROME.", driverType, e.getMessage());
            return DRIVERS.CHROME.getDriver();
        }
    }

    private static void applyConfiguredThrottling(WebDriver driver) {
        if (!ConfigManager.throttlingEnabled()) {
            return;
        }
        if (!(driver instanceof HasDevTools)) {
            log.warn("Throttling is enabled but driver {} does not support CDP; skipping throttling.",
                    driver.getClass().getSimpleName());
            return;
        }
        new ThrottleManager(driver).apply();
        log.info("Applied configured CPU/network throttling to {}", driver.getClass().getSimpleName());
    }

    public static void cleanup() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                log.warn("Failed to quit driver during cleanup: {}", e.getMessage());
            }
            driverThreadLocal.remove();
        }
    }
}