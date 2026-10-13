package es.uco.ecoagent.llm.api;

/**
 * Provider-independent response returned by an LLM adapter.
 */
public final class LlmResponse {
    private final String provider;
    private final String model;
    private final String content;
    private final int inputTokens;
    private final int outputTokens;
    private final long latencyMillis;

    /**
     * Creates an immutable model response with usage metrics.
     */
    public LlmResponse(String provider, String model, String content, int inputTokens, int outputTokens, long latencyMillis) {
        this.provider = provider;
        this.model = model;
        this.content = content;
        this.inputTokens = inputTokens;
        this.outputTokens = outputTokens;
        this.latencyMillis = latencyMillis;
    }

    /**
     * Returns the provider that produced the response.
     */
    public String getProvider() {
        return provider;
    }

    /**
     * Returns the model that produced the response.
     */
    public String getModel() {
        return model;
    }

    /**
     * Returns the raw text content produced by the model.
     */
    public String getContent() {
        return content;
    }

    /**
     * Returns the input token count reported by the provider.
     */
    public int getInputTokens() {
        return inputTokens;
    }

    /**
     * Returns the output token count reported by the provider.
     */
    public int getOutputTokens() {
        return outputTokens;
    }

    /**
     * Returns the measured or reported latency in milliseconds.
     */
    public long getLatencyMillis() {
        return latencyMillis;
    }
}

