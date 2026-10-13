package es.uco.ecoagent.llm.api;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Structured input sent to an LLM provider adapter.
 */
public final class LlmRequest {
    private final String systemInstructions;
    private final String task;
    private final String context;
    private final String outputSchema;
    private final int maxOutputTokens;
    private final double temperature;
    private final Map<String, String> metadata;

    /**
     * Creates an immutable model request.
     */
    public LlmRequest(
            String systemInstructions,
            String task,
            String context,
            String outputSchema,
            int maxOutputTokens,
            double temperature,
            Map<String, String> metadata) {
        this.systemInstructions = systemInstructions;
        this.task = task;
        this.context = context;
        this.outputSchema = outputSchema;
        this.maxOutputTokens = maxOutputTokens;
        this.temperature = temperature;
        this.metadata = Collections.unmodifiableMap(new LinkedHashMap<String, String>(metadata));
    }

    /**
     * Returns provider-independent system instructions.
     */
    public String getSystemInstructions() {
        return systemInstructions;
    }

    /**
     * Returns the user task sent to the model.
     */
    public String getTask() {
        return task;
    }

    /**
     * Returns bounded contextual data supplied to the model.
     */
    public String getContext() {
        return context;
    }

    /**
     * Returns the expected output schema description.
     */
    public String getOutputSchema() {
        return outputSchema;
    }

    /**
     * Returns the maximum output token budget.
     */
    public int getMaxOutputTokens() {
        return maxOutputTokens;
    }

    /**
     * Returns the sampling temperature requested.
     */
    public double getTemperature() {
        return temperature;
    }

    /**
     * Returns reproducibility metadata attached to the call.
     */
    public Map<String, String> getMetadata() {
        return metadata;
    }
}

