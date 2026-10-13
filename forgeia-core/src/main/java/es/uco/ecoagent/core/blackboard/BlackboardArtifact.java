package es.uco.ecoagent.core.blackboard;

/**
 * Known shared artifacts used instead of agent-to-agent conversations.
 */
public enum BlackboardArtifact {
    REQUIREMENTS("requirements.json"),
    ARCHITECTURE("architecture.json"),
    DECISIONS("decisions.json"),
    API_CONTRACT("api-contract.yaml"),
    PROJECT_GRAPH("project-graph.json"),
    TASK_STATUS("task-status.json"),
    METRICS("metrics.jsonl");

    private final String fileName;

    /**
     * Stores the file name used on disk.
     */
    BlackboardArtifact(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Returns the artifact file name inside blackboard/.
     */
    public String getFileName() {
        return fileName;
    }
}

