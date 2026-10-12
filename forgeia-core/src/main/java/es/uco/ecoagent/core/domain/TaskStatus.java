package es.uco.ecoagent.core.domain;

/**
 * Enumerates the state machine values used by executable tasks.
 */
public enum TaskStatus {
    PENDING,
    PLANNED,
    READY,
    RUNNING,
    VERIFYING,
    DONE,
    FAILED
}

