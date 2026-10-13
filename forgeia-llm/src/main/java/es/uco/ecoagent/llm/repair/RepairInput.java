package es.uco.ecoagent.llm.repair;

import es.uco.ecoagent.tools.verification.NormalizedError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Input passed to the repair agent after deterministic verification fails.
 */
public final class RepairInput {
    private final String taskId;
    private final String previousDiff;
    private final List<NormalizedError> errors;

    /**
     * Creates a repair request with prior diff and normalized errors only.
     */
    public RepairInput(String taskId, String previousDiff, List<NormalizedError> errors) {
        this.taskId = taskId;
        this.previousDiff = previousDiff;
        this.errors = Collections.unmodifiableList(new ArrayList<NormalizedError>(errors));
    }

    /**
     * Returns the task being repaired.
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * Returns the previous diff that failed verification.
     */
    public String getPreviousDiff() {
        return previousDiff;
    }

    /**
     * Returns compact normalized errors from Maven.
     */
    public List<NormalizedError> getErrors() {
        return errors;
    }
}

