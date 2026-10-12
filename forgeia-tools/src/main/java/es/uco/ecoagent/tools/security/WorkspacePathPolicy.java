package es.uco.ecoagent.tools.security;

import es.uco.ecoagent.tools.api.ToolException;

import java.nio.file.Path;

/**
 * Resolves user-provided paths without allowing workspace escape.
 */
public final class WorkspacePathPolicy {
    private final Path workspace;

    /**
     * Stores the normalized root allowed for all file operations.
     */
    public WorkspacePathPolicy(Path workspace) {
        this.workspace = workspace.toAbsolutePath().normalize();
    }

    /**
     * Resolves a relative path and rejects absolute or escaping paths.
     */
    public Path resolveInsideWorkspace(String relativePath) {
        if (relativePath == null || relativePath.trim().isEmpty()) {
            throw new ToolException("Path is required");
        }
        Path input = workspace.getFileSystem().getPath(relativePath);
        if (input.isAbsolute()) {
            throw new ToolException("Absolute paths are not allowed: " + relativePath);
        }
        Path resolved = workspace.resolve(input).normalize();
        if (!resolved.startsWith(workspace)) {
            throw new ToolException("Path escapes workspace: " + relativePath);
        }
        return resolved;
    }
}

