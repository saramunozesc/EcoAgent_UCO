package es.uco.ecoagent.llm.api;

/**
 * Abstraction for any model provider used by ForgeIA.
 */
public interface LlmClient {
    /**
     * Completes a request without exposing provider SDK details to the rest of the system.
     */
    LlmResponse complete(LlmRequest request);
}

