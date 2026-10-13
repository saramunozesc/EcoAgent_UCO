package es.uco.ecoagent.core.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a proposed or applied patch linked to a task.
 */
public final class Patch {
    private final String taskId;
    private final String diff;
    private final List<String> files;
    private final String hash;
    private final boolean applied;

    /**
     * Creates an immutable patch artifact for traceability.
     */
    public Patch(String taskId, String diff, List<String> files, String hash, boolean applied) {
        this.taskId = taskId;
        this.diff = diff;
        this.files = Collections.unmodifiableList(new ArrayList<String>(files));
        this.hash = hash;
        this.applied = applied;
    }

    /**
     * Returns the task that produced this patch.
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * Returns the unified diff content.
     */
    public String getDiff() {
        return diff;
    }

    /**
     * Returns files modified by the patch.
     */
    public List<String> getFiles() {
        return files;
    }

    /**
     * Returns the patch hash used for reproducibility.
     */
    public String getHash() {
        return hash;
    }

    /**
     * Returns whether the patch was applied to disk.
     */
    public boolean isApplied() {
        return applied;
    }
}
