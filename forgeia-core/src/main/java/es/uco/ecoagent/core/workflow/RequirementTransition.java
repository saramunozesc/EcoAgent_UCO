package es.uco.ecoagent.core.workflow;

import es.uco.ecoagent.core.domain.RequirementStatus;

import java.time.Instant;

/**
 * Records one allowed requirement state transition.
 */
public final class RequirementTransition {
    private final String requirementId;
    private final RequirementStatus from;
    private final RequirementStatus to;
    private final String reason;
    private final Instant occurredAt;

    /**
     * Creates an immutable transition entry.
     */
    public RequirementTransition(String requirementId, RequirementStatus from, RequirementStatus to,
            String reason, Instant occurredAt) {
        this.requirementId = requirementId;
        this.from = from;
        this.to = to;
        this.reason = reason;
        this.occurredAt = occurredAt;
    }

    /**
     * Returns the requirement identifier.
     */
    public String getRequirementId() {
        return requirementId;
    }

    /**
     * Returns the previous status.
     */
    public RequirementStatus getFrom() {
        return from;
    }

    /**
     * Returns the next status.
     */
    public RequirementStatus getTo() {
        return to;
    }

    /**
     * Returns the transition reason.
     */
    public String getReason() {
        return reason;
    }

    /**
     * Returns when the transition occurred.
     */
    public Instant getOccurredAt() {
        return occurredAt;
    }
}
