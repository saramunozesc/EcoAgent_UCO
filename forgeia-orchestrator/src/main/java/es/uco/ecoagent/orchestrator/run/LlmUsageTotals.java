package es.uco.ecoagent.orchestrator.run;

/**
 * Aggregated model usage captured during a run.
 */
public final class LlmUsageTotals {
    private final int calls;
    private final int inputTokens;
    private final int outputTokens;
    private final long latencyMillis;

    public LlmUsageTotals(int calls, int inputTokens, int outputTokens, long latencyMillis) {
        this.calls = calls;
        this.inputTokens = inputTokens;
        this.outputTokens = outputTokens;
        this.latencyMillis = latencyMillis;
    }

    public int getCalls() {
        return calls;
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
}
