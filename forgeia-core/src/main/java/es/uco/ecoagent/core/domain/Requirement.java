package es.uco.ecoagent.core.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Requirement {
    private final String id;
    private final String title;
    private final String description;
    private final List<String> acceptanceCriteria;
    private final RequirementPriority priority;
    private final RequirementStatus status;

    public Requirement(
            String id,
            String title,
            String description,
            List<String> acceptanceCriteria,
            RequirementPriority priority,
            RequirementStatus status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.acceptanceCriteria = Collections.unmodifiableList(new ArrayList<String>(acceptanceCriteria));
        this.priority = priority;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getAcceptanceCriteria() {
        return acceptanceCriteria;
    }

    public RequirementPriority getPriority() {
        return priority;
    }

    public RequirementStatus getStatus() {
        return status;
    }
}

