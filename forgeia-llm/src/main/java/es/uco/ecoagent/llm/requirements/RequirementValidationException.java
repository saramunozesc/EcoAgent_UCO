package es.uco.ecoagent.llm.requirements;

/**
 * Signals that an LLM response does not match the requirement schema.
 */
public final class RequirementValidationException extends RuntimeException {
    /**
     * Creates a validation exception with a compact reason.
     */
    public RequirementValidationException(String message) {
        super(message);
    }
}

