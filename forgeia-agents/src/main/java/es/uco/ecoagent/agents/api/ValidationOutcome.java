package es.uco.ecoagent.agents.api;

/**
 * Represents whether a structured output can be applied safely.
 */
public final class ValidationOutcome {
    private final boolean valid;
    private final String message;

    /**
     * Creates an immutable validation outcome.
     */
    private ValidationOutcome(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    /**
     * Creates an accepted validation outcome.
     */
    public static ValidationOutcome valid() {
        return new ValidationOutcome(true, "");
    }

    /**
     * Creates a rejected validation outcome.
     */
    public static ValidationOutcome invalid(String message) {
        return new ValidationOutcome(false, message);
    }

    /**
     * Returns whether the output is safe to apply.
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Returns the validation message.
     */
    public String getMessage() {
        return message;
    }
}
