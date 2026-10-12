package es.uco.ecoagent.tools.api;

/**
 * Signals a deterministic tool failure with a controlled message.
 */
public final class ToolException extends RuntimeException {
    /**
     * Creates a tool exception with a human-readable reason.
     */
    public ToolException(String message) {
        super(message);
    }

    /**
     * Creates a tool exception preserving the underlying cause.
     */
    public ToolException(String message, Throwable cause) {
        super(message, cause);
    }
}

