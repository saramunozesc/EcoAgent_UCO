package es.uco.ecoagent.core.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Task {
    private final String id;
    private final String requirementId;
    private final String type;
    private final TaskStatus status;
    private final List<String> dependencies;
    private final TaskBudget budget;

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

    public String getId() {
        return id;
    }

    public String getRequirementId() {
        return requirementId;
    }

    public String getType() {
        return type;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public TaskBudget getBudget() {
        return budget;
    }
}

