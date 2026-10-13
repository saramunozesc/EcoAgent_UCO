package es.uco.ecoagent.agents.api;

/**
 * Validates DTO or schema-backed agent output before side effects.
 */
public interface StructuredOutputValidator<T> {
    /**
     * Validates output and explains rejection when unsafe.
     */
    ValidationOutcome validate(T output);
}
