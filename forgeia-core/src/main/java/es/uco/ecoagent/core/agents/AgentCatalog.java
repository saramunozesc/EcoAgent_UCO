package es.uco.ecoagent.core.agents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Provides the known multi-agent roles for the orchestrator.
 */
public final class AgentCatalog {
    private final List<AgentDefinition> definitions;

    /**
     * Creates a catalog with explicit agent definitions.
     */
    public AgentCatalog(List<AgentDefinition> definitions) {
        this.definitions = Collections.unmodifiableList(new ArrayList<AgentDefinition>(definitions));
    }

    /**
     * Returns the default phase 6 catalog.
     */
    public static AgentCatalog defaults() {
        List<AgentDefinition> values = new ArrayList<AgentDefinition>();
        values.add(new AgentDefinition(AgentRole.REQUIREMENTS, true));
        values.add(new AgentDefinition(AgentRole.ARCHITECT, true));
        values.add(new AgentDefinition(AgentRole.BACKEND, true));
        values.add(new AgentDefinition(AgentRole.FRONTEND, true));
        values.add(new AgentDefinition(AgentRole.TEST, true));
        values.add(new AgentDefinition(AgentRole.REVIEWER, true));
        values.add(new AgentDefinition(AgentRole.REPAIR, true));
        return new AgentCatalog(values);
    }

    /**
     * Returns immutable agent definitions.
     */
    public List<AgentDefinition> getDefinitions() {
        return definitions;
    }

    /**
     * Finds one definition by role.
     */
    public AgentDefinition require(AgentRole role) {
        for (AgentDefinition definition : definitions) {
            if (definition.getRole() == role) {
                return definition;
            }
        }
        throw new IllegalArgumentException("Unknown agent role: " + role);
    }
}

