package es.uco.ecoagent.agents.api;

import es.uco.ecoagent.analysis.context.ContextSlice;
import es.uco.ecoagent.core.domain.TaskBudget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Carries project, task, budget, permissions and trace data for agents.
 */
public final class AgentContext {
    private final String projectId;
    private final String taskId;
    private final TaskBudget tokenBudget;
    private final List<String> toolPermissions;
    private final ContextSlice contextSlice;
    private final String traceId;

    /**
     * Creates an immutable context for one agent execution.
     */
    public AgentContext(String projectId, String taskId, TaskBudget tokenBudget, List<String> toolPermissions,
            ContextSlice contextSlice, String traceId) {
        this.projectId = projectId;
        this.taskId = taskId;
        this.tokenBudget = tokenBudget;
        this.toolPermissions = Collections.unmodifiableList(new ArrayList<String>(toolPermissions));
        this.contextSlice = contextSlice;
        this.traceId = traceId;
    }

    /**
     * Returns the project identifier.
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * Returns the task identifier.
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * Returns token and call limits for the task.
     */
    public TaskBudget getTokenBudget() {
        return tokenBudget;
    }

    /**
     * Returns tool names this agent may use.
     */
    public List<String> getToolPermissions() {
        return toolPermissions;
    }

    /**
     * Returns the selected context slice.
     */
    public ContextSlice getContextSlice() {
        return contextSlice;
    }

    /**
     * Returns the trace identifier for logs and metrics.
     */
    public String getTraceId() {
        return traceId;
    }
}
