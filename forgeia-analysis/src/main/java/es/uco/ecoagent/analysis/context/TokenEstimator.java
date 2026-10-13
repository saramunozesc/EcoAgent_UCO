package es.uco.ecoagent.analysis.context;

/**
 * Estimates tokens cheaply from character count for budget decisions.
 */
public final class TokenEstimator {
    /**
     * Estimates tokens using a conservative four characters per token rule.
     */
    public int estimate(String text) {
        if (text == null || text.length() == 0) {
            return 0;
        }
        return Math.max(1, (int) Math.ceil(text.length() / 4.0d));
    }
}

