package es.uco.ecoagent.tools.api;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Auditable summary of one deterministic tool execution.
 */
public final class ToolExecutionRecord {
    private final String toolName;
    private final String sanitizedInput;
    private final long durationMillis;
    private final String result;
    private final String error;
    private final List<String> affectedFiles;
    private final Instant executedAt;

    /**
     * Creates an immutable execution record.
     */
    public ToolExecutionRecord(String toolName, String sanitizedInput, long durationMillis, String result,
            String error, List<String> affectedFiles, Instant executedAt) {
        this.toolName = toolName;
        this.sanitizedInput = sanitizedInput;
        this.durationMillis = durationMillis;
        this.result = result;
        this.error = error;
        this.affectedFiles = Collections.unmodifiableList(new ArrayList<String>(affectedFiles));
        this.executedAt = executedAt;
    }

    /**
     * Returns the tool name.
     */
    public String getToolName() {
        return toolName;
    }

    /**
     * Returns the sanitized input summary.
     */
    public String getSanitizedInput() {
        return sanitizedInput;
    }

    /**
     * Returns execution duration in milliseconds.
     */
    public long getDurationMillis() {
        return durationMillis;
    }

    /**
     * Returns a compact result summary.
     */
    public String getResult() {
        return result;
    }

    /**
     * Returns the error summary or empty string.
     */
    public String getError() {
        return error;
    }

    /**
     * Returns files affected by the execution.
     */
    public List<String> getAffectedFiles() {
        return affectedFiles;
    }

    /**
     * Returns when the execution completed.
     */
    public Instant getExecutedAt() {
        return executedAt;
    }
}
