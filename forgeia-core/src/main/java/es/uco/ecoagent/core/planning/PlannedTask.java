package es.uco.ecoagent.core.planning;

import es.uco.ecoagent.core.domain.TaskBudget;
import es.uco.ecoagent.core.domain.TaskStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents one small planned task with likely target files.
 */
public final class PlannedTask {
    private final String id;
    private final String requirementId;
    private final String title;
    private final String type;
    private final TaskStatus status;
    private final List<String> targetFiles;
    private final List<String> dependencies;
    private final TaskBudget budget;

    /**
     * Creates an immutable planned task ready for execution tracking.
     */
    public PlannedTask(
            String id,
            String requirementId,
            String title,
            String type,
            TaskStatus status,
            List<String> targetFiles,
            List<String> dependencies,
            TaskBudget budget) {
        this.id = id;
        this.requirementId = requirementId;
        this.title = title;
        this.type = type;
        this.status = status;
        this.targetFiles = Collections.unmodifiableList(new ArrayList<String>(targetFiles));
        this.dependencies = Collections.unmodifiableList(new ArrayList<String>(dependencies));
        this.budget = budget;
    }

    /**
     * Returns the deterministic task identifier.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the requirement that originated the task.
     */
    public String getRequirementId() {
        return requirementId;
    }

    /**
     * Returns the human-readable task title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the task category used by executors and agents.
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the initial or current task status.
     */
    public TaskStatus getStatus() {
        return status;
    }

    /**
     * Returns files that are likely to be affected by this task.
     */
    public List<String> getTargetFiles() {
        return targetFiles;
    }

    /**
     * Returns task identifiers that must run before this task.
     */
    public List<String> getDependencies() {
        return dependencies;
    }

    /**
     * Returns token and retry limits assigned to this task.
     */
    public TaskBudget getBudget() {
        return budget;
    }
}

