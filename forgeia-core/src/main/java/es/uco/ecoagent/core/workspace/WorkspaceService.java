package es.uco.ecoagent.core.workspace;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class WorkspaceService {
    private final Path workspaceRoot;

    public WorkspaceService(Path workspaceRoot) {
        this.workspaceRoot = workspaceRoot.toAbsolutePath().normalize();
    }

    public Path createProjectWorkspace(String projectName) {
        String safeName = sanitize(projectName);
        Path workspace = workspaceRoot.resolve(safeName).normalize();
        if (!workspace.startsWith(workspaceRoot)) {
            throw new IllegalArgumentException("Project workspace escapes sandbox: " + projectName);
        }

        try {
            Files.createDirectories(workspace);
            return workspace;
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot create project workspace: " + workspace, exception);
        }
    }

    private static String sanitize(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name is required");
        }
        if (name.contains("..") || name.contains("/") || name.contains("\\")) {
            throw new IllegalArgumentException("Project name contains unsafe path characters: " + name);
        }
        String safeName = name.trim().toLowerCase().replaceAll("[^a-z0-9._-]+", "-");
        safeName = safeName.replaceAll("^-+", "").replaceAll("-+$", "");
        if (safeName.isEmpty() || ".".equals(safeName) || "..".equals(safeName)) {
            throw new IllegalArgumentException("Project name does not produce a safe workspace name: " + name);
        }
        return safeName;
    }
}

