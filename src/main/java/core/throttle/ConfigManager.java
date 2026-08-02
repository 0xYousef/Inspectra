package core.throttle;

import data.exceptions.ConfigurationException;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.Locale;

/**
 * Resolves throttling configuration from a strict 4-layer chain, first match wins:
 * <ol>
 *     <li>System property (e.g. {@code -Dthrottle.enabled=true})</li>
 *     <li>Environment variable (e.g. {@code THROTTLE_ENABLED=true})</li>
 *     <li>{@code .env} file on the classpath/working directory</li>
 *     <li>Built-in default</li>
 * </ol>
 * <p>
 * The {@code application.properties} file is intentionally <b>not</b> consulted.
 * Invalid values throw {@link ConfigurationException}.
 */
public final class ConfigManager {

    private static final String KEY_ENABLED = "throttle.enabled";
    private static final String KEY_CPU_PROFILE = "throttle.cpu.profile";
    private static final String KEY_NETWORK_PROFILE = "throttle.network.profile";

    private static final String DEFAULT_ENABLED = "false";
    private static final String DEFAULT_CPU_PROFILE = "NORMAL";

    private ConfigManager() {
        // static utility class - no instances
    }

    private static final class Holder {
        private static final Dotenv DOTENV = Dotenv.configure()
                .ignoreIfMissing()
                .load();
    }

    /**
     * @return {@code true} when throttling is enabled in configuration
     */
    public static boolean throttlingEnabled() {
        return Boolean.parseBoolean(resolve(KEY_ENABLED, DEFAULT_ENABLED));
    }

    /**
     * Resolves the configured CPU profile.
     *
     * @return the configured {@link CpuProfile}, never {@code null}
     * @throws ConfigurationException when the value is not a valid profile name
     */
    public static CpuProfile getCpuProfile() {
        String value = resolve(KEY_CPU_PROFILE, DEFAULT_CPU_PROFILE);
        return parseProfile(CpuProfile.class, value, KEY_CPU_PROFILE);
    }

    /**
     * Resolves the configured network profile.
     *
     * @return the configured {@link NetworkProfile}, or {@code null} when none is set
     * @throws ConfigurationException when the value is not a valid profile name
     */
    public static NetworkProfile getNetworkProfile() {
        String value = resolve(KEY_NETWORK_PROFILE, null);
        if (value == null || value.isBlank()) {
            return null;
        }
        return parseProfile(NetworkProfile.class, value, KEY_NETWORK_PROFILE);
    }

    private static String resolve(final String key, final String defaultValue) {
        String value = System.getProperty(key);
        if (value != null && !value.isBlank()) {
            return value.trim();
        }
        value = System.getenv(envName(key));
        if (value != null && !value.isBlank()) {
            return value.trim();
        }
        value = Holder.DOTENV.get(key);
        if (value != null && !value.isBlank()) {
            return value.trim();
        }
        return defaultValue;
    }

    private static String envName(final String dottedKey) {
        return dottedKey.toUpperCase(Locale.ROOT).replace('.', '_');
    }

    private static <E extends Enum<E>> E parseProfile(final Class<E> type,
                                                      final String value,
                                                      final String key) {
        String normalized = value.trim().toUpperCase(Locale.ROOT).replace('-', '_');
        for (E constant : type.getEnumConstants()) {
            if (constant.name().equals(normalized)) {
                return constant;
            }
        }
        throw new ConfigurationException(
                "Invalid value '" + value + "' for key '" + key
                        + "'. Allowed: " + java.util.Arrays.toString(type.getEnumConstants()));
    }
}
