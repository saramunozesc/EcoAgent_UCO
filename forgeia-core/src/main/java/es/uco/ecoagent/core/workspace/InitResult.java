package es.uco.ecoagent.core.workspace;

import es.uco.ecoagent.core.domain.Project;

import java.nio.file.Path;

public final class InitResult {
    private final Project project;
    private final String runId;
    private final Path logFile;

    public InitResult(Project project, String runId, Path logFile) {
        this.project = project;
        this.runId = runId;
        this.logFile = logFile;
    }

    public Project getProject() {
        return project;
    }

    public String getRunId() {
        return runId;
    }

    public Path getLogFile() {
        return logFile;
    }
}

