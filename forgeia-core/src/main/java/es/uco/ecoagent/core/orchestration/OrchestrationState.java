package es.uco.ecoagent.core.orchestration;

/**
 * States used by the explicit multi-agent orchestrator.
 */
public enum OrchestrationState {
    NEW,
    REQUIREMENTS_READY,
    ARCHITECTURE_READY,
    TASKS_READY,
    PATCH_READY,
    VERIFYING,
    DONE,
    NEEDS_REVIEW
}

