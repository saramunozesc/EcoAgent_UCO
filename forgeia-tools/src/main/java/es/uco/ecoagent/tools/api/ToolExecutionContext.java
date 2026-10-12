package es.uco.ecoagent.tools.api;

import es.uco.ecoagent.tools.security.WorkspacePathPolicy;

import java.nio.file.Path;

/**
 * Carries the authorized workspace and policies for a tool call.
 */
public final class ToolExecutionContext {
    private final Path workspace;
    private final WorkspacePathPolicy pathPolicy;

    /**
     * Creates a context bound to one normalized workspace root.
     */
    public ToolExecutionContext(Path workspace) {
        this.workspace = workspace.toAbsolutePath().normalize();
        this.pathPolicy = new WorkspacePathPolicy(this.workspace);
    }

    /**
     * Returns the authorized workspace root.
     */
    public Path getWorkspace() {
        return workspace;
    }

    /**
     * Returns the path policy used to constrain filesystem operations.
     */
    public WorkspacePathPolicy getPathPolicy() {
        return pathPolicy;
    }
}

