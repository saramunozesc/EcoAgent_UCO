package es.uco.ecoagent.tools.security;

import es.uco.ecoagent.tools.api.ToolException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

/**
 * Resolves user-provided paths without allowing workspace escape.
 */
public final class WorkspacePathPolicy {
    private final Path workspace;
    private final Path realWorkspace;

    /**
     * Stores the normalized root allowed for all file operations.
     */
    public WorkspacePathPolicy(Path workspace) {
        this.workspace = workspace.toAbsolutePath().normalize();
        if (!Files.isDirectory(this.workspace)) {
            throw new ToolException("Workspace must be an existing directory: " + this.workspace);
        }
        try {
            this.realWorkspace = this.workspace.toRealPath();
        } catch (IOException exception) {
            throw new ToolException("Cannot resolve workspace: " + this.workspace, exception);
        }
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
        rejectSymbolicLinkEscape(resolved, relativePath);
        return resolved;
    }

    /**
     * Resolves the closest existing ancestor to prevent a symlink or junction
     * from redirecting an apparently safe path outside the workspace.
     */
    private void rejectSymbolicLinkEscape(Path resolved, String originalPath) {
        Path existing = resolved;
        while (existing != null && !Files.exists(existing, LinkOption.NOFOLLOW_LINKS)) {
            existing = existing.getParent();
        }
        if (existing == null) {
            throw new ToolException("Cannot validate path: " + originalPath);
        }
        try {
            Path realExisting = existing.toRealPath();
            if (!realExisting.startsWith(realWorkspace)) {
                throw new ToolException("Path resolves outside workspace: " + originalPath);
            }
        } catch (IOException exception) {
            throw new ToolException("Cannot validate path: " + originalPath, exception);
        }
    }
}

