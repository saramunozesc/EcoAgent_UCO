package es.uco.ecoagent.llm.code;

import es.uco.ecoagent.core.planning.PlannedTask;

/**
 * Input sent to the first code agent for one planned task.
 */
public final class CodeTaskInput {
    private final PlannedTask task;
    private final String architectureInstructions;
    private final String contextSlice;

    /**
     * Creates a code task request with bounded architecture and context data.
     */
    public CodeTaskInput(PlannedTask task, String architectureInstructions, String contextSlice) {
        this.task = task;
        this.architectureInstructions = architectureInstructions;
        this.contextSlice = contextSlice;
    }

    /**
     * Returns the planned task to implement.
     */
    public PlannedTask getTask() {
        return task;
    }

    /**
     * Returns architecture guidance supplied to the code agent.
     */
    public String getArchitectureInstructions() {
        return architectureInstructions;
    }

    /**
     * Returns the minimal context selected for this task.
     */
    public String getContextSlice() {
        return contextSlice;
    }
}

