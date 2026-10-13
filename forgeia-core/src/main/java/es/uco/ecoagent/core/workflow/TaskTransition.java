package es.uco.ecoagent.core.workflow;

import es.uco.ecoagent.core.domain.TaskStatus;

import java.time.Instant;

/**
 * Records one state transition in the task state machine.
 */
public final class TaskTransition {
    private final String taskId;
    private final TaskStatus fromStatus;
    private final TaskStatus toStatus;
    private final String reason;
    private final Instant occurredAt;

    /**
     * Creates an immutable transition audit record.
     */
    public TaskTransition(String taskId, TaskStatus fromStatus, TaskStatus toStatus, String reason, Instant occurredAt) {
        this.taskId = taskId;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.reason = reason;
        this.occurredAt = occurredAt;
    }

    /**
     * Returns the task whose status changed.
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * Returns the previous status.
     */
    public TaskStatus getFromStatus() {
        return fromStatus;
    }

    /**
     * Returns the new status.
     */
    public TaskStatus getToStatus() {
        return toStatus;
    }

    /**
     * Returns the reason recorded for the transition.
     */
    public String getReason() {
        return reason;
    }

    /**
     * Returns when the transition happened.
     */
    public Instant getOccurredAt() {
        return occurredAt;
    }
}

