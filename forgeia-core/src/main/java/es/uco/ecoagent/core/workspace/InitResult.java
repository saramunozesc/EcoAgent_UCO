package es.uco.ecoagent.core.workspace;

import es.uco.ecoagent.core.domain.Project;

import java.nio.file.Path;

/**
 * Captures the visible result of running forgeia init.
 */
public final class InitResult {
    private final Project project;
    private final String runId;
    private final Path logFile;

    /**
     * Stores the created project, run identifier and log location.
     */
    public InitResult(Project project, String runId, Path logFile) {
        this.project = project;
        this.runId = runId;
        this.logFile = logFile;
    }

    /**
     * Returns the project created during initialization.
     */
    public Project getProject() {
        return project;
    }

    /**
     * Returns the unique identifier assigned to this execution.
     */
    public String getRunId() {
        return runId;
    }

    /**
     * Returns the JSONL file where the execution was recorded.
     */
    public Path getLogFile() {
        return logFile;
    }
}

