package es.uco.ecoagent.tools.search;

/**
 * Represents one textual match in a workspace file.
 */
public final class SearchMatch {
    private final String path;
    private final int lineNumber;
    private final String line;

    /**
     * Stores the matched file, one-based line number and line text.
     */
    public SearchMatch(String path, int lineNumber, String line) {
        this.path = path;
        this.lineNumber = lineNumber;
        this.line = line;
    }

    /**
     * Returns the workspace-relative matched file path.
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns the one-based line number of the match.
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Returns the line text containing the match.
     */
    public String getLine() {
        return line;
    }
}

