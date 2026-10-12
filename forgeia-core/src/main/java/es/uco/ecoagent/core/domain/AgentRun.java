package es.uco.ecoagent.core.domain;

import java.time.Instant;

public final class AgentRun {
    private final String id;
    private final String agentType;
    private final String taskId;
    private final Instant startedAt;
    private final String result;

    public AgentRun(String id, String agentType, String taskId, Instant startedAt, String result) {
        this.id = id;
        this.agentType = agentType;
        this.taskId = taskId;
        this.startedAt = startedAt;
        this.result = result;
    }

    public String getId() {
        return id;
    }

    public String getAgentType() {
        return agentType;
    }

    public String getTaskId() {
        return taskId;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public String getResult() {
        return result;
    }
}

