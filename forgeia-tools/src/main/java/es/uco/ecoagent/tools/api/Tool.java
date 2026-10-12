package es.uco.ecoagent.tools.api;

/**
 * Defines a deterministic operation available to future agents.
 */
public interface Tool<I, O> {
    /**
     * Returns the stable tool name used in logs and allowlists.
     */
    String name();

    /**
     * Executes the tool with structured input and output.
     */
    O execute(ToolExecutionContext context, I input);
}

