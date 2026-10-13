package es.uco.ecoagent.core.orchestration;

import es.uco.ecoagent.core.agents.AgentRole;

import java.time.Instant;

/**
 * Audits one orchestrator transition and the agent that produced it.
 */
public final class OrchestrationTransition {
    private final OrchestrationState fromState;
    private final OrchestrationState toState;
    private final AgentRole agentRole;
    private final String artifact;
    private final Instant occurredAt;

    /**
     * Creates an immutable orchestration transition.
     */
    public OrchestrationTransition(OrchestrationState fromState, OrchestrationState toState, AgentRole agentRole, String artifact, Instant occurredAt) {
        this.fromState = fromState;
        this.toState = toState;
        this.agentRole = agentRole;
        this.artifact = artifact;
        this.occurredAt = occurredAt;
    }

    /**
     * Returns the previous orchestrator state.
     */
    public OrchestrationState getFromState() {
        return fromState;
    }

    /**
     * Returns the new orchestrator state.
     */
    public OrchestrationState getToState() {
        return toState;
    }

    /**
     * Returns the agent role responsible for the transition.
     */
    public AgentRole getAgentRole() {
        return agentRole;
    }

    /**
     * Returns the artifact produced or consumed by the transition.
     */
    public String getArtifact() {
        return artifact;
    }

    /**
     * Returns when the transition happened.
     */
    public Instant getOccurredAt() {
        return occurredAt;
    }
}

