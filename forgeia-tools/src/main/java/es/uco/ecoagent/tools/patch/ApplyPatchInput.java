package es.uco.ecoagent.tools.patch;

/**
 * Input containing a unified diff to apply to the workspace.
 */
public final class ApplyPatchInput {
    private final String unifiedDiff;

    /**
     * Creates a patch application request.
     */
    public ApplyPatchInput(String unifiedDiff) {
        this.unifiedDiff = unifiedDiff;
    }

    /**
     * Returns the unified diff text.
     */
    public String getUnifiedDiff() {
        return unifiedDiff;
    }
}

