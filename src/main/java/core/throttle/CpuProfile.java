package core.throttle;

/**
 * CPU throttling presets for Chrome DevTools Protocol.
 * <p>
 * Each constant maps to a CPU slow-down multiplier used by
 * {@code Emulation.setCPUThrottlingRate}. A multiplier of {@code 1}
 * (i.e. {@link #NORMAL}) disables CPU throttling. Use
 * {@link #custom(int)} to define an arbitrary rate.
 */
public enum CpuProfile implements CpuThrottle {

    /** No CPU throttling. */
    NORMAL(1),

    /** CPU 2x slower. */
    X2(2),

    /** CPU 4x slower. */
    X4(4),

    /** CPU 6x slower. */
    X6(6),

    /** CPU 8x slower. */
    X8(8),

    /** CPU 10x slower. */
    X10(10),

    /** CPU 20x slower. */
    X20(20);

    private final int multiplier;

    CpuProfile(final int multiplier) {
        this.multiplier = multiplier;
    }

    /**
     * Creates a custom CPU rate with an arbitrary slow-down multiplier.
     *
     * @param multiplier the CPU slow-down rate, {@code >= 1}; {@code 1} disables throttling
     * @return a custom CPU rate
     * @throws IllegalArgumentException when {@code multiplier < 1}
     */
    public static CpuThrottle custom(final int multiplier) {
        if (multiplier < 1) {
            throw new IllegalArgumentException("CPU multiplier must be >= 1, was " + multiplier);
        }
        return () -> multiplier;
    }

    /**
     * @return the CPU slow-down multiplier ({@code Emulation.setCPUThrottlingRate} value)
     */
    @Override
    public int multiplier() {
        return multiplier;
    }

    @Override
    public String toString() {
        return "CpuProfile[" + name() + " x" + multiplier + "]";
    }
}
