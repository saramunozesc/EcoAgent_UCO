package es.uco.ecoagent.evaluation.metrics;

import java.math.BigDecimal;

/**
 * Aggregates quality, token and repair metrics for one project.
 */
public final class ProjectMetricsSummary {
    private final int llmCalls;
    private final int inputTokens;
    private final int outputTokens;
    private final int contextTokens;
    private final int testsPassed;
    private final int testsFailed;
    private final int repairAttempts;
    private final int requirementsCompleted;
    private final BigDecimal estimatedCost;
    private final double buildSuccessRate;

    /**
     * Creates an immutable project metrics summary.
     */
    public ProjectMetricsSummary(int llmCalls, int inputTokens, int outputTokens, int contextTokens,
            int testsPassed, int testsFailed, int repairAttempts, int requirementsCompleted,
            BigDecimal estimatedCost, double buildSuccessRate) {
        this.llmCalls = llmCalls;
        this.inputTokens = inputTokens;
        this.outputTokens = outputTokens;
        this.contextTokens = contextTokens;
        this.testsPassed = testsPassed;
        this.testsFailed = testsFailed;
        this.repairAttempts = repairAttempts;
        this.requirementsCompleted = requirementsCompleted;
        this.estimatedCost = estimatedCost;
        this.buildSuccessRate = buildSuccessRate;
    }

    /**
     * Returns the number of LLM calls counted.
     */
    public int getLlmCalls() {
        return llmCalls;
    }

    /**
     * Returns total input tokens.
     */
    public int getInputTokens() {
        return inputTokens;
    }

    /**
     * Returns total output tokens.
     */
    public int getOutputTokens() {
        return outputTokens;
    }

    /**
     * Returns selected context tokens.
     */
    public int getContextTokens() {
        return contextTokens;
    }

    /**
     * Returns total passed tests.
     */
    public int getTestsPassed() {
        return testsPassed;
    }

    /**
     * Returns total failed tests.
     */
    public int getTestsFailed() {
        return testsFailed;
    }

    /**
     * Returns total repair attempts.
     */
    public int getRepairAttempts() {
        return repairAttempts;
    }

    /**
     * Returns completed requirements.
     */
    public int getRequirementsCompleted() {
        return requirementsCompleted;
    }

    /**
     * Returns estimated monetary cost.
     */
    public BigDecimal getEstimatedCost() {
        return estimatedCost;
    }

    /**
     * Returns build success rate from 0.0 to 1.0.
     */
    public double getBuildSuccessRate() {
        return buildSuccessRate;
    }
}
