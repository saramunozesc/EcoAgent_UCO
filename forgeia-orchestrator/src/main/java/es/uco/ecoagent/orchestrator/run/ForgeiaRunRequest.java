package es.uco.ecoagent.orchestrator.run;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Input for one bounded ForgeIA implementation run.
 */
public final class ForgeiaRunRequest {
    private final Path workspace;
    private final String idea;
    private final String architectureInstructions;
    private final int requirementIndex;
    private final int taskIndex;

    /**
     * Creates a request for the first generated requirement and task.
     */
    public ForgeiaRunRequest(Path workspace, String idea, String architectureInstructions) {
        this(workspace, idea, architectureInstructions, 0, 0);
    }

    /**
     * Creates a request selecting one requirement and one planned task.
     */
    public ForgeiaRunRequest(Path workspace, String idea, String architectureInstructions,
            int requirementIndex, int taskIndex) {
        if (workspace == null || !Files.isDirectory(workspace)) {
            throw new IllegalArgumentException("Workspace must be an existing directory");
        }
        if (idea == null || idea.trim().isEmpty()) {
            throw new IllegalArgumentException("Idea must not be empty");
        }
        if (requirementIndex < 0 || taskIndex < 0) {
            throw new IllegalArgumentException("Requirement and task indexes must be non-negative");
        }
        this.workspace = workspace.toAbsolutePath().normalize();
        this.idea = idea.trim();
        this.architectureInstructions = architectureInstructions == null
                ? "Java con Maven; cambios pequenos y compatibles con el proyecto existente"
                : architectureInstructions.trim();
        this.requirementIndex = requirementIndex;
        this.taskIndex = taskIndex;
    }

    public Path getWorkspace() {
        return workspace;
    }

    public String getIdea() {
        return idea;
    }

    public String getArchitectureInstructions() {
        return architectureInstructions;
    }

    public int getRequirementIndex() {
        return requirementIndex;
    }

    public int getTaskIndex() {
        return taskIndex;
    }
}
