package es.uco.ecoagent.analysis.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Minimal selected context for one task under a token budget.
 */
public final class ContextSlice {
    private final String taskId;
    private final List<ContextItem> items;
    private final int tokenCount;
    private final String strategy;

    /**
     * Creates an immutable context slice.
     */
    public ContextSlice(String taskId, List<ContextItem> items, int tokenCount, String strategy) {
        this.taskId = taskId;
        this.items = Collections.unmodifiableList(new ArrayList<ContextItem>(items));
        this.tokenCount = tokenCount;
        this.strategy = strategy;
    }

    /**
     * Returns the task for which the context was built.
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * Returns selected context items.
     */
    public List<ContextItem> getItems() {
        return items;
    }

    /**
     * Returns total estimated tokens.
     */
    public int getTokenCount() {
        return tokenCount;
    }

    /**
     * Returns the selection strategy name.
     */
    public String getStrategy() {
        return strategy;
    }
}

