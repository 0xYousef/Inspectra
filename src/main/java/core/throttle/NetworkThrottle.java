package core.throttle;

/**
 * A network throttling profile: either a named {@link NetworkProfile} preset or a
 * custom profile created with {@link NetworkProfile#custom(int, int, int)}.
 */
public interface NetworkThrottle {

    /**
     * @return the throttled settings
     */
    NetworkSettings settings();
}
