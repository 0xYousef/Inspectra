package core.throttle;

/**
 * Network throttling presets emulating common mobile and legacy connections.
 * <p>
 * Speeds are expressed in <b>bits per second</b> in the JavaDoc and converted to
 * <b>bytes per second</b> internally, matching the units expected by
 * {@code Network.emulateNetworkConditions}. Use {@link #custom(int, int, int)}
 * to define an arbitrary profile with raw <b>bytes per second</b> values.
 *
 * @see NetworkSettings
 */
public enum NetworkProfile implements NetworkThrottle {

    /** Wi-Fi: 30 Mbps down / 15 Mbps up, 5 ms latency. */
    WIFI(30_000_000, 15_000_000, 5),

    /** Fast 4G: 30 Mbps down / 15 Mbps up, 20 ms latency. */
    FAST_4G(30_000_000, 15_000_000, 20),

    /** 4G: 20 Mbps down / 10 Mbps up, 40 ms latency. */
    FOUR_G(20_000_000, 10_000_000, 40),

    /** Slow 4G: 400 kbps down / 400 kbps up, 150 ms latency. */
    SLOW_4G(400_000, 400_000, 150),

    /** Fast 3G: 1.6 Mbps down / 750 kbps up, 75 ms latency. */
    FAST_3G(1_600_000, 750_000, 75),

    /** 3G: 1.6 Mbps down / 750 kbps up, 100 ms latency. */
    THREE_G(1_600_000, 750_000, 100),

    /** Slow 3G: 400 kbps down / 400 kbps up, 200 ms latency. */
    SLOW_3G(400_000, 400_000, 200),

    /** 2G: 250 kbps down / 50 kbps up, 300 ms latency. */
    TWO_G(250_000, 50_000, 300),

    /** EDGE: 240 kbps down / 200 kbps up, 300 ms latency. */
    EDGE(240_000, 200_000, 300),

    /** GPRS: 50 kbps down / 20 kbps up, 500 ms latency. */
    GPRS(50_000, 20_000, 500),

    /** Fully disconnected network. */
    OFFLINE(0, 0, 0);

    private final int downloadThroughputBytesPerSecond;
    private final int uploadThroughputBytesPerSecond;
    private final int latencyMillis;

    NetworkProfile(final long downloadBitsPerSecond,
                   final long uploadBitsPerSecond,
                   final int latencyMillis) {
        this.downloadThroughputBytesPerSecond = toBytes(downloadBitsPerSecond);
        this.uploadThroughputBytesPerSecond = toBytes(uploadBitsPerSecond);
        this.latencyMillis = latencyMillis;
    }

    private static int toBytes(final long bitsPerSecond) {
        return (int) (bitsPerSecond / 8);
    }

    /**
     * Creates a custom network profile from raw CDP values.
     *
     * @param downloadBytesPerSecond download throughput in bytes per second ({@code >= 0}; {@code 0} = unlimited)
     * @param uploadBytesPerSecond   upload throughput in bytes per second ({@code >= 0}; {@code 0} = unlimited)
     * @param latencyMillis          additional round-trip latency in milliseconds ({@code >= 0})
     * @return a custom network profile
     * @throws IllegalArgumentException when any value is negative
     */
    public static NetworkThrottle custom(final int downloadBytesPerSecond,
                                         final int uploadBytesPerSecond,
                                         final int latencyMillis) {
        if (downloadBytesPerSecond < 0 || uploadBytesPerSecond < 0 || latencyMillis < 0) {
            throw new IllegalArgumentException(
                    "Custom network values must be >= 0 (download=" + downloadBytesPerSecond
                            + " B/s, upload=" + uploadBytesPerSecond + " B/s, latency="
                            + latencyMillis + " ms)");
        }
        return () -> new NetworkSettings(false, latencyMillis, downloadBytesPerSecond,
                uploadBytesPerSecond);
    }

    /**
     * @return the throttled profile as {@link NetworkSettings}
     */
    @Override
    public NetworkSettings settings() {
        if (this == OFFLINE) {
            return NetworkSettings.offlineSettings();
        }
        return new NetworkSettings(false, latencyMillis, downloadThroughputBytesPerSecond,
                uploadThroughputBytesPerSecond);
    }

    /**
     * @return the round-trip latency in milliseconds
     */
    public int latencyMillis() {
        return latencyMillis;
    }

    /**
     * @return the download throughput in bytes per second
     */
    public int downloadThroughputBytesPerSecond() {
        return downloadThroughputBytesPerSecond;
    }

    /**
     * @return the upload throughput in bytes per second
     */
    public int uploadThroughputBytesPerSecond() {
        return uploadThroughputBytesPerSecond;
    }

    @Override
    public String toString() {
        return "NetworkProfile[" + name() + "]";
    }
}
