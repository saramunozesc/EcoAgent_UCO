package es.uco.ecoagent.core.planning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Contains the deterministic task plan for one requirement.
 */
public final class RequirementPlan {
    private final String requirementId;
    private final List<PlannedTask> tasks;

    /**
     * Creates an immutable requirement plan.
     */
    public RequirementPlan(String requirementId, List<PlannedTask> tasks) {
        this.requirementId = requirementId;
        this.tasks = Collections.unmodifiableList(new ArrayList<PlannedTask>(tasks));
    }

    /**
     * Returns the requirement covered by this plan.
     */
    public String getRequirementId() {
        return requirementId;
    }

    /**
     * Returns the ordered tasks that implement the requirement.
     */
    public List<PlannedTask> getTasks() {
        return tasks;
    }
}

