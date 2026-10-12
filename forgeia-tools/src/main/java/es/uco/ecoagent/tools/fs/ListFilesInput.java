package es.uco.ecoagent.tools.fs;

/**
 * Input for listing files below a relative path.
 */
public final class ListFilesInput {
    private final String path;
    private final String contains;

    /**
     * Creates a list request with an optional substring filter.
     */
    public ListFilesInput(String path, String contains) {
        this.path = path;
        this.contains = contains;
    }

    /**
     * Returns the relative directory or file to inspect.
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns the optional substring that file paths must contain.
     */
    public String getContains() {
        return contains;
    }
}

