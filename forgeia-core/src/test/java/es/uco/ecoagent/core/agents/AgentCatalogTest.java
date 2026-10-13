package es.uco.ecoagent.core.agents;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Verifies the phase 6 agent catalog.
 */
public class AgentCatalogTest {
    /**
     * Ensures all expected specialized roles are registered.
     */
    @Test
    public void defaultCatalogContainsAllRoles() {
        AgentCatalog catalog = AgentCatalog.defaults();

        assertEquals(7, catalog.getDefinitions().size());
        assertEquals("RequirementSpec", catalog.require(AgentRole.REQUIREMENTS).getRole().getOutputArtifact());
        assertTrue(catalog.require(AgentRole.BACKEND).isUsesLlm());
    }
}

