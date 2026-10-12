package es.uco.ecoagent.tools.fs;

/**
 * Structured result produced after writing a file.
 */
public final class WriteFileResult {
    private final String path;
    private final String sha256;

    /**
     * Stores the written path and resulting content hash.
     */
    public WriteFileResult(String path, String sha256) {
        this.path = path;
        this.sha256 = sha256;
    }

    /**
     * Returns the workspace-relative path written.
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns the SHA-256 hash of the written content.
     */
    public String getSha256() {
        return sha256;
    }
}

