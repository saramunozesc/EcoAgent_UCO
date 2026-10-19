package es.uco.ecoagent.llm.requirements;

import es.uco.ecoagent.llm.api.LlmClient;
import es.uco.ecoagent.llm.api.LlmRequest;
import es.uco.ecoagent.llm.api.LlmResponse;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Test double that returns queued LLM responses deterministically.
 */
public final class FakeLlmClient implements LlmClient {
    private final Queue<LlmResponse> responses = new ArrayDeque<LlmResponse>();
    private int calls;
    private LlmRequest lastRequest;

    /**
     * Adds a response that will be returned by the next complete call.
     */
    public FakeLlmClient add(String content, int inputTokens, int outputTokens) {
        responses.add(new LlmResponse("fake", "fake-requirements", content, inputTokens, outputTokens, 10L));
        return this;
    }

    /**
     * Returns the next queued response and records the call count.
     */
    public LlmResponse complete(LlmRequest request) {
        calls++;
        lastRequest = request;
        return responses.remove();
    }

    /**
     * Returns how many times the fake client was called.
     */
    public int getCalls() {
        return calls;
    }

    /**
     * Returns the most recent request for prompt-boundary assertions.
     */
    public LlmRequest getLastRequest() {
        return lastRequest;
    }
}

