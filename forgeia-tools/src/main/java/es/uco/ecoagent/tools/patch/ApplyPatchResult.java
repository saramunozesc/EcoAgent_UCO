package es.uco.ecoagent.tools.patch;

import java.util.Collections;
import java.util.List;

/**
 * Structured result for a patch application.
 */
public final class ApplyPatchResult {
    private final int exitCode;
    private final List<String> modifiedFiles;
    private final String output;

    /**
     * Stores command status, affected files and bounded output.
     */
    public ApplyPatchResult(int exitCode, List<String> modifiedFiles, String output) {
        this.exitCode = exitCode;
        this.modifiedFiles = Collections.unmodifiableList(modifiedFiles);
        this.output = output;
    }

    /**
     * Returns the exit code from git apply.
     */
    public int getExitCode() {
        return exitCode;
    }

    /**
     * Returns workspace-relative files mentioned by the diff.
     */
    public List<String> getModifiedFiles() {
        return modifiedFiles;
    }

    /**
     * Returns bounded output from patch application.
     */
    public String getOutput() {
        return output;
    }
}

