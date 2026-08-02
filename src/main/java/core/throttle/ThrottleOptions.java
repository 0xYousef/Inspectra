package core.throttle;

import java.util.Objects;

/**
 * Immutable, composite throttling configuration composed of an optional CPU
 * profile and an optional network profile/settings.
 * <p>
 * Instances are created through the nested {@link Builder}. At least one of the
 * two dimensions must be present, otherwise {@link Builder#build()} throws an
 * {@link IllegalArgumentException}.
 */
public final class ThrottleOptions {

    private final CpuThrottle cpuProfile;
    private final NetworkSettings networkSettings;

    private ThrottleOptions(final CpuThrottle cpuProfile, final NetworkSettings networkSettings) {
        this.cpuProfile = cpuProfile;
        this.networkSettings = networkSettings;
    }

    /**
     * @return the CPU profile, or {@code null} if not configured
     */
    public CpuThrottle cpuProfile() {
        return cpuProfile;
    }

    /**
     * @return the network settings, or {@code null} if not configured
     */
    public NetworkSettings networkSettings() {
        return networkSettings;
    }

    /**
     * @return {@code true} when a CPU profile is configured
     */
    public boolean hasCpu() {
        return cpuProfile != null;
    }

    /**
     * @return {@code true} when network settings are configured
     */
    public boolean hasNetwork() {
        return networkSettings != null;
    }

    /**
     * Builder for {@link ThrottleOptions}.
     */
    public static final class Builder {

        private CpuThrottle cpuProfile;
        private NetworkSettings networkSettings;

        /**
         * @param cpuRate the CPU slow-down profile or custom rate
         * @return this builder
         */
        public Builder cpu(final CpuThrottle cpuRate) {
            this.cpuProfile = Objects.requireNonNull(cpuRate, "cpuRate must not be null");
            return this;
        }

        /**
         * @param networkProfile a named network preset or custom rate
         * @return this builder
         */
        public Builder network(final NetworkThrottle networkProfile) {
            this.networkSettings = Objects.requireNonNull(networkProfile, "networkProfile must not be null")
                    .settings();
            return this;
        }

        /**
         * @param networkSettings raw network emulation settings
         * @return this builder
         */
        public Builder network(final NetworkSettings networkSettings) {
            this.networkSettings = Objects.requireNonNull(networkSettings, "networkSettings must not be null");
            return this;
        }

        /**
         * Builds the immutable options.
         *
         * @return a new {@link ThrottleOptions}
         * @throws IllegalArgumentException when neither a CPU profile nor network
         *                                  settings have been configured
         */
        public ThrottleOptions build() {
            if (cpuProfile == null && networkSettings == null) {
                throw new IllegalArgumentException(
                        "At least one of cpuProfile or networkSettings must be set");
            }
            return new ThrottleOptions(cpuProfile, networkSettings);
        }
    }
}
