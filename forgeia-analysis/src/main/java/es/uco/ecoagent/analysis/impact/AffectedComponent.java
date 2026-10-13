package es.uco.ecoagent.analysis.impact;

/**
 * Represents one component likely affected by a new requirement.
 */
public final class AffectedComponent {
    private final String path;
    private final String symbol;
    private final double score;
    private final String reason;

    /**
     * Creates an immutable impact item.
     */
    public AffectedComponent(String path, String symbol, double score, String reason) {
        this.path = path;
        this.symbol = symbol;
        this.score = score;
        this.reason = reason;
    }

    /**
     * Returns the source file path when known.
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns the Java symbol or component name.
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Returns the deterministic ranking score.
     */
    public double getScore() {
        return score;
    }

    /**
     * Returns a short explanation for the ranking.
     */
    public String getReason() {
        return reason;
    }
}
