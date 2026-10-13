package es.uco.ecoagent.analysis.project;

import java.nio.file.Path;

/**
 * Captures the existing project root and label used during import.
 */
public final class ProjectImportRequest {
    private final Path projectRoot;
    private final String projectName;

    /**
     * Creates an immutable import request.
     */
    public ProjectImportRequest(Path projectRoot, String projectName) {
        this.projectRoot = projectRoot;
        this.projectName = projectName;
    }

    /**
     * Returns the Maven project directory to import.
     */
    public Path getProjectRoot() {
        return projectRoot;
    }

    /**
     * Returns the short name used in generated metadata.
     */
    public String getProjectName() {
        return projectName;
    }
}
