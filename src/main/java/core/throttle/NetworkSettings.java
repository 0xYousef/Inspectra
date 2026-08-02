package core.throttle;

/**
 * Immutable network emulation settings for Chrome DevTools Protocol.
 * <p>
 * Values map directly to {@code Network.emulateNetworkConditions}:
 * <ul>
 *     <li>{@code offline} &mdash; emulate a fully disconnected state</li>
 *     <li>{@code latency} &mdash; round-trip delay in milliseconds</li>
 *     <li>{@code downloadThroughput} &mdash; download speed in <b>bytes per second</b></li>
 *     <li>{@code uploadThroughput} &mdash; upload speed in <b>bytes per second</b></li>
 * </ul>
 * A throughput value of {@code -1} (or {@code 0} when the profile is not offline)
 * instructs Chrome to leave that direction unthrottled.
 */
public record NetworkSettings(
        boolean offline,
        int latency,
        int downloadThroughput,
        int uploadThroughput) {

    /**
     * Compact constructor enforcing valid {@code emulateNetworkConditions} input.
     *
     * @throws IllegalArgumentException if {@code latency} is negative or any
     *                                  throughput is less than {@code -1}
     */
    public NetworkSettings {
        if (latency < 0) {
            throw new IllegalArgumentException("latency must be >= 0 but was " + latency);
        }
        if (downloadThroughput < -1) {
            throw new IllegalArgumentException(
                    "downloadThroughput must be >= -1 but was " + downloadThroughput);
        }
        if (uploadThroughput < -1) {
            throw new IllegalArgumentException(
                    "uploadThroughput must be >= -1 but was " + uploadThroughput);
        }
    }

    /**
     * Convenience factory for a fully offline connection.
     *
     * @return settings with {@code offline = true} and zero throughput
     */
    public static NetworkSettings offlineSettings() {
        return new NetworkSettings(true, 0, 0, 0);
    }
}
