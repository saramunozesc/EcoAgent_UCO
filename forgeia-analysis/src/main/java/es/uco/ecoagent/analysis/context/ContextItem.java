package es.uco.ecoagent.analysis.context;

/**
 * One selected context unit with score, level and token estimate.
 */
public final class ContextItem {
    private final String path;
    private final ContextLevel level;
    private final String content;
    private final int tokenEstimate;
    private final double score;

    /**
     * Creates an immutable context item.
     */
    public ContextItem(String path, ContextLevel level, String content, int tokenEstimate, double score) {
        this.path = path;
        this.level = level;
        this.content = content;
        this.tokenEstimate = tokenEstimate;
        this.score = score;
    }

    /**
     * Returns the source path for the context item.
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns the selected context level.
     */
    public ContextLevel getLevel() {
        return level;
    }

    /**
     * Returns the text that will be sent to the agent.
     */
    public String getContent() {
        return content;
    }

    /**
     * Returns the estimated token count.
     */
    public int getTokenEstimate() {
        return tokenEstimate;
    }

    /**
     * Returns the ranking score used for selection.
     */
    public double getScore() {
        return score;
    }
}

