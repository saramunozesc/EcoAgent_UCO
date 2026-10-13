package es.uco.ecoagent.evaluation.metrics;

/**
 * Enumerates the phase 9 metrics recorded by ForgeIA.
 */
public enum MetricName {
    INPUT_TOKENS,
    OUTPUT_TOKENS,
    CONTEXT_TOKENS,
    LLM_CALLS,
    LATENCY_MILLIS,
    ESTIMATED_COST,
    BUILD_SUCCESS,
    TESTS_PASSED,
    TESTS_FAILED,
    REPAIR_ATTEMPTS,
    REQUIREMENTS_COMPLETED,
    CONTEXT_ITEMS_USED,
    ACCEPTANCE_CRITERIA_MET,
    UNRELATED_CHANGES
}
