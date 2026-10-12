package es.uco.ecoagent.tools.fs;

/**
 * Input for writing UTF-8 text into a workspace file.
 */
public final class WriteFileInput {
    private final String path;
    private final String content;

    /**
     * Creates a write request for a relative file path.
     */
    public WriteFileInput(String path, String content) {
        this.path = path;
        this.content = content;
    }

    /**
     * Returns the workspace-relative target file.
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns the UTF-8 content to persist.
     */
    public String getContent() {
        return content;
    }
}

