package data.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ExceptionHandler.class);

    private ExceptionHandler() {
    }

    public static <T> T handle(Throwable e, String context) {
        log.error("Exception in {}: {}", context, e.getMessage(), e);
        throw new FrameworkException("Exception in " + context + ": " + e.getMessage(), e);
    }

    public static <T> T handle(Throwable e, String context, FrameworkException exception) {
        log.error("Exception in {}: {}", context, e.getMessage(), e);
        throw exception;
    }

    public static void handleSilently(Throwable e, String context) {
        log.warn("Non-fatal exception in {}: {}", context, e.getMessage());
    }
}
