package es.uco.ecoagent.core.workflow;

import es.uco.ecoagent.core.domain.TaskStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Enforces valid task status transitions and records every change.
 */
public final class TaskStateMachine {
    private final String taskId;
    private TaskStatus status;
    private final List<TaskTransition> history = new ArrayList<TaskTransition>();
    private static final Map<TaskStatus, List<TaskStatus>> ALLOWED = allowedTransitions();

    /**
     * Starts a state machine for one task at its current status.
     */
    public TaskStateMachine(String taskId, TaskStatus initialStatus) {
        this.taskId = taskId;
        this.status = initialStatus;
    }

    /**
     * Moves to a new status if the transition is allowed.
     */
    public TaskTransition transitionTo(TaskStatus nextStatus, String reason) {
        List<TaskStatus> nextStatuses = ALLOWED.get(status);
        if (nextStatuses == null || !nextStatuses.contains(nextStatus)) {
            throw new IllegalStateException("Invalid transition from " + status + " to " + nextStatus);
        }
        TaskTransition transition = new TaskTransition(taskId, status, nextStatus, reason, Instant.now());
        history.add(transition);
        status = nextStatus;
        return transition;
    }

    /**
     * Returns the current task status.
     */
    public TaskStatus getStatus() {
        return status;
    }

    /**
     * Returns the immutable transition history.
     */
    public List<TaskTransition> getHistory() {
        return Collections.unmodifiableList(history);
    }

    /**
     * Defines the deterministic workflow allowed by phase 3.
     */
    private static Map<TaskStatus, List<TaskStatus>> allowedTransitions() {
        Map<TaskStatus, List<TaskStatus>> transitions = new EnumMap<TaskStatus, List<TaskStatus>>(TaskStatus.class);
        transitions.put(TaskStatus.PENDING, Collections.singletonList(TaskStatus.PLANNED));
        transitions.put(TaskStatus.PLANNED, Collections.singletonList(TaskStatus.READY));
        transitions.put(TaskStatus.READY, Collections.singletonList(TaskStatus.RUNNING));
        transitions.put(TaskStatus.RUNNING, asList(TaskStatus.VERIFYING, TaskStatus.FAILED));
        transitions.put(TaskStatus.VERIFYING, asList(TaskStatus.DONE, TaskStatus.FAILED));
        transitions.put(TaskStatus.FAILED, Collections.singletonList(TaskStatus.RUNNING));
        transitions.put(TaskStatus.DONE, Collections.<TaskStatus>emptyList());
        return transitions;
    }

    /**
     * Creates a mutable-free list for transition definitions.
     */
    private static List<TaskStatus> asList(TaskStatus first, TaskStatus second) {
        List<TaskStatus> values = new ArrayList<TaskStatus>();
        values.add(first);
        values.add(second);
        return Collections.unmodifiableList(values);
    }
}

