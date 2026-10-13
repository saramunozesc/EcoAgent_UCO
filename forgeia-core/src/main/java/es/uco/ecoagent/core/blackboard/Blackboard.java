package es.uco.ecoagent.core.blackboard;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * File-backed shared memory for structured multi-agent artifacts.
 */
public final class Blackboard {
    private final Path root;

    /**
     * Creates a blackboard rooted at workspace/blackboard.
     */
    public Blackboard(Path workspaceRoot) {
        this.root = workspaceRoot.toAbsolutePath().normalize().resolve("blackboard");
    }

    /**
     * Ensures all known artifact files can be placed under blackboard/.
     */
    public void initialize() {
        try {
            Files.createDirectories(root);
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot initialize blackboard: " + root, exception);
        }
    }

    /**
     * Writes or replaces one structured artifact.
     */
    public Path write(BlackboardArtifact artifact, String content) {
        initialize();
        Path path = resolve(artifact);
        try {
            Files.write(path, content.getBytes(StandardCharsets.UTF_8));
            return path;
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot write blackboard artifact: " + artifact, exception);
        }
    }

    /**
     * Appends one line to a JSONL metrics artifact.
     */
    public Path appendMetric(String jsonLine) {
        initialize();
        Path path = resolve(BlackboardArtifact.METRICS);
        try {
            Files.write(path, (jsonLine + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
                    java.nio.file.StandardOpenOption.CREATE,
                    java.nio.file.StandardOpenOption.APPEND);
            return path;
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot append metrics artifact", exception);
        }
    }

    /**
     * Reads one artifact as UTF-8 text.
     */
    public String read(BlackboardArtifact artifact) {
        try {
            return new String(Files.readAllBytes(resolve(artifact)), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot read blackboard artifact: " + artifact, exception);
        }
    }

    /**
     * Resolves an artifact path inside blackboard/.
     */
    public Path resolve(BlackboardArtifact artifact) {
        return root.resolve(artifact.getFileName()).normalize();
    }

    /**
     * Returns the blackboard root directory.
     */
    public Path getRoot() {
        return root;
    }
}

