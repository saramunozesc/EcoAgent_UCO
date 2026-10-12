package es.uco.ecoagent.tools.fs;

/**
 * Input for reading a file with an optional one-based line range.
 */
public final class ReadFileInput {
    private final String path;
    private final Integer startLine;
    private final Integer endLine;

    /**
     * Creates a read request for a whole file or inclusive line range.
     */
    public ReadFileInput(String path, Integer startLine, Integer endLine) {
        this.path = path;
        this.startLine = startLine;
        this.endLine = endLine;
    }

    /**
     * Returns the workspace-relative file path.
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns the optional first line to read.
     */
    public Integer getStartLine() {
        return startLine;
    }

    /**
     * Returns the optional last line to read.
     */
    public Integer getEndLine() {
        return endLine;
    }
}

