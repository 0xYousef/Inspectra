package core.base;

import core.utils.Configuration;
import core.utils.Screenshot;
import core.utils.SessionRecorder;
import org.openqa.selenium.WebDriver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;


public class BaseUITest {
    private static final String URL = Configuration.get("ui.base.url");
    private static final Logger log = LoggerFactory.getLogger(BaseUITest.class);
    protected WebDriver driver;
    private final DRIVERS driverType;
    private final DEVICES device;
    private SessionRecorder recorder;

    public BaseUITest(DRIVERS driverType) {
        this(driverType, DEVICES.MAXSIZE); // Default to MAXSIZE
    }

    public BaseUITest(DRIVERS driverType, DEVICES device) {
        this.driverType = driverType;
        this.device = device;
    }

    @BeforeClass
    public void openSession() {
        driver = DriverFactory.setup(driverType, device);
        driver.get(URL);
        log.info("Opened browser session for {}", getClass().getSimpleName());
        recorder = SessionRecorder.start(driver, getClass().getSimpleName());
    }

    @AfterMethod
    public void captureEvidence(ITestResult result) {
        boolean passed = result != null && result.isSuccess();
        Screenshot.capture(driver, getClass().getSimpleName(), passed ? "PASS" : "FAIL");
        if (recorder != null) {
            recorder.stop(!passed);
        }
    }

    @AfterClass
    public void closeSession() {
        if (recorder != null) {
            recorder.stop(false);
            recorder = null;
        }
        if (driver != null) {
            try {
                driver.quit();
                log.info("Closed browser session for {}", getClass().getSimpleName());
            } catch (Exception e) {
                log.warn("Failed to close session for {}: {}", getClass().getSimpleName(), e.getMessage());
            }
        }
    }

    @AfterSuite
    public void teardown() {
        DriverFactory.cleanup();
    }
}
