package selenium.tests.throttle;

import core.base.DriverFactory;
import core.throttle.CpuProfile;
import core.throttle.NetworkProfile;
import core.throttle.NetworkSettings;
import core.throttle.ThrottleManager;
import core.throttle.ThrottleOptions;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Demo tests for the CPU/network throttling feature.
 * <p>
 * These tests are intentionally kept out of the default TestNG suites so they
 * do not run on every CI build. They require a local Chrome/ChromeDriver
 * installation.
 */
public class ThrottleDemoTest {

    private WebDriver driver;

    @BeforeMethod
    public void setUp() {
        driver = DriverFactory.create();
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Relies on the {@code throttle.enabled} configuration flag; the CPU and
     * network profiles are read from configuration and applied automatically by
     * {@link DriverFactory#create()}.
     */
    @Test
    public void autoApplyFromConfiguration() {
        driver.get("https://example.com");
    }

    /**
     * Manually overrides the configured throttling for this session.
     */
    @Test
    public void manualOverrideWithProfiles() {
        ThrottleManager throttle = new ThrottleManager(driver);
        throttle.apply(CpuProfile.X4, NetworkProfile.THREE_G);
        driver.get("https://example.com");
        throttle.reset();
    }

    /**
     * Uses the fluent {@link ThrottleOptions.Builder}.
     */
    @Test
    public void applyWithThrottleOptionsBuilder() {
        ThrottleOptions options = new ThrottleOptions.Builder()
                .cpu(CpuProfile.X2)
                .network(NetworkProfile.FAST_3G)
                .build();
        new ThrottleManager(driver).apply(options);
        driver.get("https://example.com");
    }

    /**
     * Applies raw network emulation settings without touching the CPU.
     */
    @Test
    public void customNetworkSettings() {
        NetworkSettings settings = new NetworkSettings(false, 250, 500_000, 250_000);
        new ThrottleManager(driver).setNetwork(settings);
        driver.get("https://example.com");
    }
}
