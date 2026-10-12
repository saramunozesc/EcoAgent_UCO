package es.uco.ecoagent.core.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Describes a user requirement with criteria and workflow metadata.
 */
public final class Requirement {
    private final String id;
    private final String title;
    private final String description;
    private final List<String> acceptanceCriteria;
    private final RequirementPriority priority;
    private final RequirementStatus status;

    /**
     * Creates an immutable requirement definition.
     */
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

    /**
     * Returns the deterministic requirement identifier.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the short requirement title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the detailed requirement description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the acceptance criteria used to validate the requirement.
     */
    public List<String> getAcceptanceCriteria() {
        return acceptanceCriteria;
    }

    /**
     * Returns the priority assigned to the requirement.
     */
    public RequirementPriority getPriority() {
        return priority;
    }

    /**
     * Returns the current workflow status of the requirement.
     */
    public RequirementStatus getStatus() {
        return status;
    }
}

