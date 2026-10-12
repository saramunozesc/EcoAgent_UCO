package es.uco.ecoagent.core.domain;

import java.nio.file.Path;
import java.time.Instant;

public final class Project {
    private final String id;
    private final String name;
    private final Path workspace;
    private final ProjectStatus status;
    private final Instant createdAt;

    public Project(String id, String name, Path workspace, ProjectStatus status, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.workspace = workspace;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Path getWorkspace() {
        return workspace;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}

