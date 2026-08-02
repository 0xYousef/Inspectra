package core.throttle;

/**
 * A CPU throttling rate: either a named {@link CpuProfile} preset or an arbitrary
 * rate created with {@link CpuProfile#custom(int)}.
 */
public interface CpuThrottle {

    /**
     * @return the CPU slow-down multiplier ({@code Emulation.setCPUThrottlingRate} value)
     */
    int multiplier();
}
