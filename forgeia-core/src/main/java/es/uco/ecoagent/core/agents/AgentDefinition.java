package es.uco.ecoagent.core.agents;

/**
 * Describes one specialized agent without binding it to an implementation.
 */
public final class AgentDefinition {
    private final AgentRole role;
    private final boolean usesLlm;

    /**
     * Creates an immutable agent definition.
     */
    public AgentDefinition(AgentRole role, boolean usesLlm) {
        this.role = role;
        this.usesLlm = usesLlm;
    }

    /**
     * Returns the agent role.
     */
    public AgentRole getRole() {
        return role;
    }

    /**
     * Returns whether this agent is expected to call an LLM.
     */
    public boolean isUsesLlm() {
        return usesLlm;
    }
}

