package es.uco.ecoagent.core.orchestration;

import es.uco.ecoagent.core.agents.AgentRole;
import es.uco.ecoagent.core.blackboard.Blackboard;
import es.uco.ecoagent.core.blackboard.BlackboardArtifact;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Verifies explicit orchestration through blackboard artifacts.
 */
public class MultiAgentOrchestratorTest {
    /**
     * Ensures the orchestrator advances through the deterministic happy path.
     */
    @Test
    public void advancesThroughMultiAgentWorkflow() throws Exception {
        Path workspace = Files.createTempDirectory("orchestrator");
        MultiAgentOrchestrator orchestrator = new MultiAgentOrchestrator(new Blackboard(workspace));

        orchestrator.acceptArtifact(AgentRole.REQUIREMENTS, BlackboardArtifact.REQUIREMENTS, "{}");
        orchestrator.acceptArtifact(AgentRole.ARCHITECT, BlackboardArtifact.ARCHITECTURE, "{}");
        orchestrator.markTasksReady("{\"tasks\":[]}");
        orchestrator.acceptArtifact(AgentRole.BACKEND, BlackboardArtifact.DECISIONS, "{\"patch\":\"ok\"}");
        orchestrator.acceptArtifact(AgentRole.REVIEWER, BlackboardArtifact.TASK_STATUS, "{\"state\":\"VERIFYING\"}");
        orchestrator.advanceVerification(true, "mvn test");

        assertEquals(OrchestrationState.DONE, orchestrator.getState());
        assertEquals(6, orchestrator.getHistory().size());
        assertTrue(Files.isRegularFile(workspace.resolve("blackboard").resolve("requirements.json")));
    }

    /**
     * Ensures invalid agent order is rejected.
     */
    @Test(expected = IllegalStateException.class)
    public void rejectsInvalidAgentOrder() throws Exception {
        Path workspace = Files.createTempDirectory("orchestrator-invalid");
        MultiAgentOrchestrator orchestrator = new MultiAgentOrchestrator(new Blackboard(workspace));

        orchestrator.acceptArtifact(AgentRole.BACKEND, BlackboardArtifact.DECISIONS, "{}");
    }
}

