package es.uco.ecoagent.core.domain;

/**
 * Enumerates the workflow states a requirement can move through.
 */
public enum RequirementStatus {
    DRAFT,
    APPROVED,
    PLANNED,
    IN_PROGRESS,
    VERIFYING,
    DONE,
    BLOCKED,
    NEEDS_REVIEW
}

