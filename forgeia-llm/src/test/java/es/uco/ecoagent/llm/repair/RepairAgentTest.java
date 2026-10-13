package es.uco.ecoagent.llm.repair;

import es.uco.ecoagent.llm.code.PatchProposal;
import es.uco.ecoagent.llm.requirements.FakeLlmClient;
import es.uco.ecoagent.tools.verification.ErrorType;
import es.uco.ecoagent.tools.verification.NormalizedError;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Verifies repair proposals are generated from compact error context.
 */
public class RepairAgentTest {
    /**
     * Ensures the repair agent returns a patch proposal for the same task.
     */
    @Test
    public void proposesRepairPatch() {
        FakeLlmClient client = new FakeLlmClient()
                .add("{"
                        + "\"taskId\":\"TASK-004-3\","
                        + "\"summary\":\"Corrige llamada a repositorio\","
                        + "\"unifiedDiff\":\"diff --git a/UserService.java b/UserService.java\\n@@\\n-findByUsername\\n+findByEmail\","
                        + "\"files\":[\"UserService.java\"]"
                        + "}", 80, 40);
        NormalizedError error = new NormalizedError(
                ErrorType.COMPILATION_ERROR,
                "UserService.java",
                47,
                "cannot find symbol findByUsername",
                "findByUsername",
                null);

        PatchProposal proposal = new RepairAgent(client).proposeRepair(
                new RepairInput("TASK-004-3", "previous diff", Collections.singletonList(error)));

        assertEquals("TASK-004-3", proposal.getTaskId());
        assertEquals("UserService.java", proposal.getFiles().get(0));
        assertTrue(proposal.getUnifiedDiff().contains("findByEmail"));
    }
}

