package es.uco.ecoagent.server;

/**
 * Boundary used by the HTTP server to submit a run without knowing its LLM provider.
 */
public interface RunRequestHandler {
    /**
     * Executes a validated run request and returns a JSON response.
     */
    String execute(String requestBody);
}
