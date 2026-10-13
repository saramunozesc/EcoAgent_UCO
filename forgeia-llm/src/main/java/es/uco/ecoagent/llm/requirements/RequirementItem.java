package es.uco.ecoagent.llm.requirements;

import es.uco.ecoagent.core.domain.RequirementPriority;

import java.util.Collections;
import java.util.List;

/**
 * One validated requirement in a generated requirement specification.
 */
public final class RequirementItem {
    private final String id;
    private final String title;
    private final List<String> acceptanceCriteria;
    private final RequirementPriority priority;

    /**
     * Creates an immutable requirement item.
     */
    public RequirementItem(String id, String title, List<String> acceptanceCriteria, RequirementPriority priority) {
        this.id = id;
        this.title = title;
        this.acceptanceCriteria = Collections.unmodifiableList(acceptanceCriteria);
        this.priority = priority;
    }

    /**
     * Returns the deterministic requirement ID assigned outside the LLM.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the requirement title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns acceptance criteria for validation and later tests.
     */
    public List<String> getAcceptanceCriteria() {
        return acceptanceCriteria;
    }

    /**
     * Returns the normalized requirement priority.
     */
    public RequirementPriority getPriority() {
        return priority;
    }
}

