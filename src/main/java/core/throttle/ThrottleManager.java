package core.throttle;

import data.exceptions.ThrottleException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v150.emulation.Emulation;
import org.openqa.selenium.devtools.v150.network.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;

/**
 * Applies CPU and network throttling to a single WebDriver session using the
 * Chrome DevTools Protocol.
 * <p>
 * Instances are bound to one driver and are safe to share within that driver's
 * thread; all CDP traffic is guarded by an instance monitor. The CDP session
 * is created lazily on first use and reused afterwards.
 * <p>
 * Drivers that do not implement {@link HasDevTools} (e.g. Safari or plain
 * remote drivers without CDP support) are rejected with a {@link ThrottleException}.
 */
public final class ThrottleManager {

    private static final Logger log = LoggerFactory.getLogger(ThrottleManager.class);

    private final WebDriver driver;
    private DevTools devTools;
    private boolean networkEnabled;

    /**
     * @param driver the driver to throttle
     * @throws ThrottleException when the driver does not support the DevTools protocol
     */
    public ThrottleManager(final WebDriver driver) {
        this.driver = Objects.requireNonNull(driver, "driver must not be null");
        if (!(driver instanceof HasDevTools)) {
            throw new ThrottleException(
                    "Driver " + driver.getClass().getName() + " does not support Chrome DevTools Protocol");
        }
    }

    /**
     * Applies the throttling configured via {@link ConfigManager} (system
     * property / environment variable / {@code .env}). The CPU profile always
     * has a value; the network profile is only applied when configured.
     */
    public void apply() {
        apply(ConfigManager.getCpuProfile());
        NetworkProfile networkProfile = ConfigManager.getNetworkProfile();
        if (networkProfile != null) {
            apply(networkProfile);
        }
    }

    /**
     * Applies a CPU slow-down profile or custom rate.
     *
     * @param cpuRate the profile to apply
     */
    public void apply(final CpuThrottle cpuRate) {
        setCpu(cpuRate);
    }

    /**
     * Applies a network profile or custom rate.
     *
     * @param networkProfile the profile to apply
     */
    public void apply(final NetworkThrottle networkProfile) {
        setNetwork(networkProfile);
    }

    /**
     * Applies both a CPU profile and a network profile.
     *
     * @param cpuRate         the CPU profile to apply
     * @param networkProfile the network profile to apply
     */
    public void apply(final CpuThrottle cpuRate, final NetworkThrottle networkProfile) {
        setCpu(cpuRate);
        setNetwork(networkProfile);
    }

    /**
     * Applies whichever dimensions are present in the given options.
     *
     * @param options composite options to apply
     */
    public void apply(final ThrottleOptions options) {
        Objects.requireNonNull(options, "options must not be null");
        if (options.hasCpu()) {
            setCpu(options.cpuProfile());
        }
        if (options.hasNetwork()) {
            setNetwork(options.networkSettings());
        }
    }

    /**
     * Sets the CPU slow-down multiplier.
     *
     * @param cpuRate the profile to apply; {@link CpuProfile#NORMAL} restores full speed
     */
    public void setCpu(final CpuThrottle cpuRate) {
        Objects.requireNonNull(cpuRate, "cpuRate must not be null");
        synchronized (this) {
            DevTools session = ensureDevTools();
            session.send(Emulation.setCPUThrottlingRate(cpuRate.multiplier()));
            log.info("Applied CPU throttling rate {} to driver {}", cpuRate.multiplier(),
                    driver.getClass().getSimpleName());
        }
    }

    /**
     * Applies a network profile or custom rate.
     *
     * @param networkProfile the profile to apply
     */
    public void setNetwork(final NetworkThrottle networkProfile) {
        setNetwork(Objects.requireNonNull(networkProfile, "networkProfile must not be null").settings());
    }

    /**
     * Applies raw network emulation settings.
     *
     * @param networkSettings the settings to apply
     */
    public void setNetwork(final NetworkSettings networkSettings) {
        Objects.requireNonNull(networkSettings, "networkSettings must not be null");
        synchronized (this) {
            DevTools session = ensureDevTools();
            ensureNetworkEnabled(session);
            session.send(Network.emulateNetworkConditions(
                    networkSettings.offline(),
                    networkSettings.latency(),
                    networkSettings.downloadThroughput(),
                    networkSettings.uploadThroughput(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty()));
            log.info("Applied network emulation (offline={}, latency={}ms, down={} B/s, up={} B/s) to driver {}",
                    networkSettings.offline(), networkSettings.latency(),
                    networkSettings.downloadThroughput(), networkSettings.uploadThroughput(),
                    driver.getClass().getSimpleName());
        }
    }

    /**
     * Restores full CPU speed.
     */
    public void resetCpu() {
        setCpu(CpuProfile.NORMAL);
    }

    /**
     * Restores an unthrottled connection.
     */
    public void resetNetwork() {
        setNetwork(new NetworkSettings(false, 0, -1, -1));
    }

    /**
     * Restores full CPU speed and an unthrottled connection.
     */
    public void reset() {
        resetCpu();
        resetNetwork();
    }

    private synchronized DevTools ensureDevTools() {
        if (devTools == null) {
            devTools = ((HasDevTools) driver).getDevTools();
            devTools.createSessionIfThereIsNotOne();
            log.debug("Opened CDP session for driver {}", driver.getClass().getSimpleName());
        }
        return devTools;
    }

    private void ensureNetworkEnabled(final DevTools session) {
        if (!networkEnabled) {
            session.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty(),
                    Optional.empty(), Optional.empty()));
            networkEnabled = true;
        }
    }
}
