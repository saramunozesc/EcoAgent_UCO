package es.uco.ecoagent.tools.template;

import java.nio.file.Path;

/**
 * Reports where a deterministic template was generated.
 */
public final class TemplateResult {
    private final Path projectRoot;
    private final int fileCount;

    /**
     * Stores the generated root directory and number of files written.
     */
    public TemplateResult(Path projectRoot, int fileCount) {
        this.projectRoot = projectRoot;
        this.fileCount = fileCount;
    }

    /**
     * Returns the generated project root path.
     */
    public Path getProjectRoot() {
        return projectRoot;
    }

    /**
     * Returns how many files were generated.
     */
    public int getFileCount() {
        return fileCount;
    }
}

