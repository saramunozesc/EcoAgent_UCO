package es.uco.ecoagent.tools.fs;

import java.util.Collections;
import java.util.List;

/**
 * Structured result containing workspace-relative file paths.
 */
public final class ListFilesResult {
    private final List<String> files;

    /**
     * Stores the sorted file list returned by listFiles.
     */
    public ListFilesResult(List<String> files) {
        this.files = Collections.unmodifiableList(files);
    }

    /**
     * Returns the matching file paths.
     */
    public List<String> getFiles() {
        return files;
    }
}

