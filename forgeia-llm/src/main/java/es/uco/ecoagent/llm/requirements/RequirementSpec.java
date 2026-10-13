package es.uco.ecoagent.llm.requirements;

import java.util.Collections;
import java.util.List;

/**
 * Validated requirement specification generated from a user idea.
 */
public final class RequirementSpec {
    private final String projectName;
    private final List<String> actors;
    private final List<RequirementItem> requirements;

    /**
     * Creates an immutable specification with project, actors and requirements.
     */
    public RequirementSpec(String projectName, List<String> actors, List<RequirementItem> requirements) {
        this.projectName = projectName;
        this.actors = Collections.unmodifiableList(actors);
        this.requirements = Collections.unmodifiableList(requirements);
    }

    /**
     * Returns the generated project name.
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Returns the actors identified for the project.
     */
    public List<String> getActors() {
        return actors;
    }

    /**
     * Returns validated requirements with deterministic IDs.
     */
    public List<RequirementItem> getRequirements() {
        return requirements;
    }
}

