package es.uco.ecoagent.core.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a small executable unit derived from a requirement.
 */
public final class Task {
    private final String id;
    private final String requirementId;
    private final String type;
    private final TaskStatus status;
    private final List<String> dependencies;
    private final TaskBudget budget;

    /**
     * Creates an immutable task with dependencies and token budget.
     */
    public Task(
            String id,
            String requirementId,
            String type,
            TaskStatus status,
            List<String> dependencies,
            TaskBudget budget) {
        this.id = id;
        this.requirementId = requirementId;
        this.type = type;
        this.status = status;
        this.dependencies = Collections.unmodifiableList(new ArrayList<String>(dependencies));
        this.budget = budget;
    }

    /**
     * Returns the internal task identifier.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the requirement that originated this task.
     */
    public String getRequirementId() {
        return requirementId;
    }

    /**
     * Returns the task category or execution type.
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the current state of the task execution.
     */
    public TaskStatus getStatus() {
        return status;
    }

    /**
     * Returns task IDs that must be completed before this task.
     */
    public List<String> getDependencies() {
        return dependencies;
    }

    /**
     * Returns the token and retry limits assigned to this task.
     */
    public TaskBudget getBudget() {
        return budget;
    }
}

