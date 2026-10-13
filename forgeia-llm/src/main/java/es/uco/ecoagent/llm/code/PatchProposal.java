package es.uco.ecoagent.llm.code;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Structured patch proposal returned by a code agent.
 */
public final class PatchProposal {
    private final String taskId;
    private final String summary;
    private final String unifiedDiff;
    private final List<String> files;

    /**
     * Creates an immutable proposal that has not been applied to disk.
     */
    public PatchProposal(String taskId, String summary, String unifiedDiff, List<String> files) {
        this.taskId = taskId;
        this.summary = summary;
        this.unifiedDiff = unifiedDiff;
        this.files = Collections.unmodifiableList(new ArrayList<String>(files));
    }

    /**
     * Returns the task covered by the proposal.
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * Returns a compact explanation of the intended change.
     */
    public String getSummary() {
        return summary;
    }

    /**
     * Returns the unified diff proposed by the agent.
     */
    public String getUnifiedDiff() {
        return unifiedDiff;
    }

    /**
     * Returns files expected to be modified by the diff.
     */
    public List<String> getFiles() {
        return files;
    }
}

