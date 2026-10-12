package es.uco.ecoagent.tools.maven;

import es.uco.ecoagent.tools.api.ToolException;
import org.junit.Test;

/**
 * Verifies Maven arguments are constrained before process execution.
 */
public class MavenToolTest {
    /**
     * Ensures shell-like Maven arguments are rejected.
     */
    @Test(expected = ToolException.class)
    public void rejectsUnsafeMavenGoal() {
        MavenTool.validateToken("test;rm", "goal");
    }
}

