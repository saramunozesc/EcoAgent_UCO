package es.uco.ecoagent.core.workflow;

import es.uco.ecoagent.core.domain.RequirementStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Enforces the requirement workflow defined in the internal data model.
 */
public final class RequirementStateMachine {
    private static final Map<RequirementStatus, List<RequirementStatus>> ALLOWED = allowedTransitions();

    private final String requirementId;
    private RequirementStatus status;
    private final List<RequirementTransition> history = new ArrayList<RequirementTransition>();

    /**
     * Starts a state machine for one requirement.
     */
    public RequirementStateMachine(String requirementId, RequirementStatus initialStatus) {
        this.requirementId = requirementId;
        this.status = initialStatus;
    }

    /**
     * Moves to a new status if the transition is allowed.
     */
    public RequirementTransition transitionTo(RequirementStatus nextStatus, String reason) {
        List<RequirementStatus> nextStatuses = ALLOWED.get(status);
        if (nextStatuses == null || !nextStatuses.contains(nextStatus)) {
            throw new IllegalStateException("Invalid transition from " + status + " to " + nextStatus);
        }
        RequirementTransition transition = new RequirementTransition(requirementId, status, nextStatus, reason, Instant.now());
        history.add(transition);
        status = nextStatus;
        return transition;
    }

    /**
     * Returns the current requirement status.
     */
    public RequirementStatus getStatus() {
        return status;
    }

    /**
     * Returns the immutable transition history.
     */
    public List<RequirementTransition> getHistory() {
        return Collections.unmodifiableList(history);
    }

    /**
     * Defines the deterministic requirement lifecycle.
     */
    private static Map<RequirementStatus, List<RequirementStatus>> allowedTransitions() {
        Map<RequirementStatus, List<RequirementStatus>> transitions =
                new EnumMap<RequirementStatus, List<RequirementStatus>>(RequirementStatus.class);
        transitions.put(RequirementStatus.DRAFT, Collections.singletonList(RequirementStatus.APPROVED));
        transitions.put(RequirementStatus.APPROVED, Collections.singletonList(RequirementStatus.PLANNED));
        transitions.put(RequirementStatus.PLANNED, Collections.singletonList(RequirementStatus.IN_PROGRESS));
        transitions.put(RequirementStatus.IN_PROGRESS,
                asList(RequirementStatus.VERIFYING, RequirementStatus.BLOCKED, RequirementStatus.NEEDS_REVIEW));
        transitions.put(RequirementStatus.VERIFYING,
                asList(RequirementStatus.DONE, RequirementStatus.BLOCKED, RequirementStatus.NEEDS_REVIEW));
        transitions.put(RequirementStatus.DONE, Collections.<RequirementStatus>emptyList());
        transitions.put(RequirementStatus.BLOCKED, Collections.<RequirementStatus>emptyList());
        transitions.put(RequirementStatus.NEEDS_REVIEW, Collections.<RequirementStatus>emptyList());
        return transitions;
    }

    /**
     * Creates a mutable-free list for transition definitions.
     */
    private static List<RequirementStatus> asList(RequirementStatus first, RequirementStatus second, RequirementStatus third) {
        List<RequirementStatus> values = new ArrayList<RequirementStatus>();
        values.add(first);
        values.add(second);
        values.add(third);
        return Collections.unmodifiableList(values);
    }
}
