package es.uco.ecoagent.agents.api;

/**
 * Defines one specialized agent with structured input and output.
 */
public interface Agent<I, O> {
    /**
     * Executes the agent under an auditable context.
     */
    O execute(AgentContext context, I input);
}
