package es.uco.ecoagent.core.trace;

/**
 * Enumerates artifacts that can participate in ForgeIA traceability.
 */
public enum TraceElementType {
    REQUIREMENT,
    TASK,
    PATCH,
    TEST_RESULT,
    LLM_USAGE,
    COMMIT,
    DIFF,
    CONTEXT_SLICE,
    FILE,
    BUILD
}
