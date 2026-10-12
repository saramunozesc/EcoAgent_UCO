package es.uco.ecoagent.core.domain;

import java.math.BigDecimal;

public final class LlmUsage {
    private final String provider;
    private final String model;
    private final int inputTokens;
    private final int outputTokens;
    private final long latencyMillis;
    private final BigDecimal estimatedCost;

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

    public String getProvider() {
        return provider;
    }

    public String getModel() {
        return model;
    }

    public int getInputTokens() {
        return inputTokens;
    }

    public int getOutputTokens() {
        return outputTokens;
    }

    public long getLatencyMillis() {
        return latencyMillis;
    }

    public BigDecimal getEstimatedCost() {
        return estimatedCost;
    }
}

