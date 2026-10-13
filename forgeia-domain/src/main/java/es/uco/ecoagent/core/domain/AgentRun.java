package es.uco.ecoagent.core.domain;

import java.time.Instant;

/**
 * Records the execution metadata produced by one agent invocation.
 */
public final class AgentRun {
    private final String id;
    private final String agentType;
    private final String taskId;
    private final Instant startedAt;
    private final String result;

    /**
     * Creates an immutable record of an agent run.
     */
    public AgentRun(String id, String agentType, String taskId, Instant startedAt, String result) {
        this.id = id;
        this.agentType = agentType;
        this.taskId = taskId;
        this.startedAt = startedAt;
        this.result = result;
    }

    /**
     * Returns the unique agent run identifier.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the type of agent that executed the run.
     */
    public String getAgentType() {
        return agentType;
    }

    /**
     * Returns the task associated with the run.
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * Returns when the agent run started.
     */
    public Instant getStartedAt() {
        return startedAt;
    }

    /**
     * Returns the structured or summarized result of the run.
     */
    public String getResult() {
        return result;
    }
}

