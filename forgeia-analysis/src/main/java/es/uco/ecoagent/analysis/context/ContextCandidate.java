package es.uco.ecoagent.analysis.context;

import es.uco.ecoagent.analysis.javaast.JavaFileSummary;

/**
 * Candidate source item before token-budget selection.
 */
final class ContextCandidate {
    private final JavaFileSummary summary;
    private final String fullText;
    private final double score;

    /**
     * Creates a ranked candidate from a source summary and full text.
     */
    ContextCandidate(JavaFileSummary summary, String fullText, double score) {
        this.summary = summary;
        this.fullText = fullText;
        this.score = score;
    }

    /**
     * Returns the Java summary for this candidate.
     */
    JavaFileSummary getSummary() {
        return summary;
    }

    /**
     * Returns the full source text.
     */
    String getFullText() {
        return fullText;
    }

    /**
     * Returns the ranking score.
     */
    double getScore() {
        return score;
    }
}

