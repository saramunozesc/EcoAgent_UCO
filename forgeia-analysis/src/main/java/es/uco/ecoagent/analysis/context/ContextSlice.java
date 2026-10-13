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
    private final double score;

    /**
     * Creates an immutable context slice.
     */
    public ContextSlice(String taskId, List<ContextItem> items, int tokenCount, String strategy) {
        this(taskId, items, tokenCount, strategy, averageScore(items));
    }

    /**
     * Creates an immutable context slice with aggregate ranking score.
     */
    public ContextSlice(String taskId, List<ContextItem> items, int tokenCount, String strategy, double score) {
        this.taskId = taskId;
        this.items = Collections.unmodifiableList(new ArrayList<ContextItem>(items));
        this.tokenCount = tokenCount;
        this.strategy = strategy;
        this.score = score;
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

    /**
     * Returns the aggregate score of selected context items.
     */
    public double getScore() {
        return score;
    }

    /**
     * Calculates the average selected item score.
     */
    private static double averageScore(List<ContextItem> items) {
        if (items.isEmpty()) {
            return 0.0d;
        }
        double total = 0.0d;
        for (ContextItem item : items) {
            total += item.getScore();
        }
        return total / items.size();
    }
}

