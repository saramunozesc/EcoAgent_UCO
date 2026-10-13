package es.uco.ecoagent.core.domain;

import java.math.BigDecimal;

/**
 * Captures token, latency and cost metrics for one LLM call.
 */
public final class LlmUsage {
    private final String provider;
    private final String model;
    private final int inputTokens;
    private final int outputTokens;
    private final long latencyMillis;
    private final BigDecimal estimatedCost;

    /**
     * Creates an immutable usage record for metrics and evaluation.
     */
    public LlmUsage(
            String provider,
            String model,
            int inputTokens,
            int outputTokens,
            long latencyMillis,
            BigDecimal estimatedCost) {
        this.provider = provider;
        this.model = model;
        this.inputTokens = inputTokens;
        this.outputTokens = outputTokens;
        this.latencyMillis = latencyMillis;
        this.estimatedCost = estimatedCost;
    }

    /**
     * Returns the LLM provider that handled the request.
     */
    public String getProvider() {
        return provider;
    }

    /**
     * Returns the model that handled the request.
     */
    public String getModel() {
        return model;
    }

    /**
     * Returns the number of input tokens consumed.
     */
    public int getInputTokens() {
        return inputTokens;
    }

    /**
     * Returns the number of output tokens generated.
     */
    public int getOutputTokens() {
        return outputTokens;
    }

    /**
     * Returns the latency measured for the call.
     */
    public long getLatencyMillis() {
        return latencyMillis;
    }

    /**
     * Returns the estimated monetary cost of the call.
     */
    public BigDecimal getEstimatedCost() {
        return estimatedCost;
    }
}

