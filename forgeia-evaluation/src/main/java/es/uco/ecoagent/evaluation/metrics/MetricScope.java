package es.uco.ecoagent.evaluation.metrics;

/**
 * Describes the level where an evaluation metric is measured.
 */
public enum MetricScope {
    LLM_CALL,
    TASK,
    REQUIREMENT,
    VALIDATION,
    PROJECT
}
