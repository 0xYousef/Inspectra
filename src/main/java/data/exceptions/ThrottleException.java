package data.exceptions;

/**
 * Thrown when throttling cannot be applied to a WebDriver session, for example
 * when the driver does not support the Chrome DevTools Protocol.
 */
public class ThrottleException extends FrameworkException {

    public ThrottleException(String message) {
        super(message);
    }

    public ThrottleException(String message, Throwable cause) {
        super(message, cause);
    }
}
