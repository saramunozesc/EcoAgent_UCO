package es.uco.ecoagent.llm.code;

import es.uco.ecoagent.core.domain.TaskBudget;
import es.uco.ecoagent.core.domain.TaskStatus;
import es.uco.ecoagent.core.planning.PlannedTask;
import es.uco.ecoagent.llm.requirements.FakeLlmClient;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Verifies the first code agent returns patch proposals only.
 */
public class CodeAgentTest {
    /**
     * Ensures a code response is parsed as a structured patch proposal.
     */
    @Test
    public void returnsPatchProposalWithoutApplyingIt() {
        FakeLlmClient client = new FakeLlmClient()
                .add("{"
                        + "\"taskId\":\"TASK-004-1\","
                        + "\"summary\":\"Crea entidad Score\","
                        + "\"unifiedDiff\":\"diff --git a/Score.java b/Score.java\\n--- /dev/null\\n+++ b/Score.java\\n@@\\n+class Score {}\","
                        + "\"files\":[\"Score.java\"]"
                        + "}", 100, 50);
        PlannedTask task = new PlannedTask(
                "TASK-004-1",
                "REQ-004",
                "Crear entidad Score",
                "ENTITY",
                TaskStatus.READY,
                Collections.singletonList("Score.java"),
                Collections.<String>emptyList(),
                new TaskBudget(8000, 2500, 4, 2));

        PatchProposal proposal = new CodeAgent(client).propose(new CodeTaskInput(task, "Usar Java", "Sin contexto extra"));

        assertEquals("TASK-004-1", proposal.getTaskId());
        assertEquals("Score.java", proposal.getFiles().get(0));
        assertTrue(proposal.getUnifiedDiff().contains("class Score"));
        assertEquals(1, client.getCalls());
    }
}

