package es.uco.ecoagent.core.domain;

import java.nio.file.Path;
import java.time.Instant;

/**
 * Represents a ForgeIA project and its controlled workspace.
 */
public final class Project {
    private final String id;
    private final String name;
    private final Path workspace;
    private final ProjectStatus status;
    private final Instant createdAt;

    /**
     * Creates an immutable project snapshot for orchestration and auditing.
     */
    public Project(String id, String name, Path workspace, ProjectStatus status, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.workspace = workspace;
        this.status = status;
        this.createdAt = createdAt;
    }

    /**
     * Returns the internal project identifier.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the user-facing project name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the sandbox workspace assigned to the project.
     */
    public Path getWorkspace() {
        return workspace;
    }

    /**
     * Returns the lifecycle status of the project.
     */
    public ProjectStatus getStatus() {
        return status;
    }

    /**
     * Returns when the project was created.
     */
    public Instant getCreatedAt() {
        return createdAt;
    }
}

