package es.uco.ecoagent.tools.api;

import es.uco.ecoagent.tools.security.WorkspacePathPolicy;
import es.uco.ecoagent.tools.security.DestructiveOperationPolicy;

import java.nio.file.Path;

/**
 * Carries the authorized workspace and policies for a tool call.
 */
public final class ToolExecutionContext {
    private final Path workspace;
    private final WorkspacePathPolicy pathPolicy;
    private final DestructiveOperationPolicy destructiveOperationPolicy;

    /**
     * Creates a context bound to one normalized workspace root.
     */
    public ToolExecutionContext(Path workspace) {
        this(workspace, DestructiveOperationPolicy.denyAll());
    }

    /**
     * Creates a context with explicit approvals captured by the orchestrator.
     */
    public ToolExecutionContext(Path workspace, DestructiveOperationPolicy destructiveOperationPolicy) {
        this.workspace = workspace.toAbsolutePath().normalize();
        this.pathPolicy = new WorkspacePathPolicy(this.workspace);
        this.destructiveOperationPolicy = destructiveOperationPolicy;
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

    /**
     * Requires human approval before a destructive tool mutates its target.
     */
    public void requireDestructiveApproval(String operationId, String target) {
        destructiveOperationPolicy.requireApproved(operationId, target);
    }
}

