package es.uco.ecoagent.tools.git;

/**
 * Structured result containing a bounded Git diff.
 */
public final class GitDiffResult {
    private final String diff;

    /**
     * Stores the diff text returned by gitDiff.
     */
    public GitDiffResult(String diff) {
        this.diff = diff;
    }

    /**
     * Returns the workspace diff.
     */
    public String getDiff() {
        return diff;
    }
}

