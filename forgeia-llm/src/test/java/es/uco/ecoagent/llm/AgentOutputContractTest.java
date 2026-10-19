package es.uco.ecoagent.llm;

import es.uco.ecoagent.llm.code.PatchProposal;
import es.uco.ecoagent.llm.code.PatchProposalParser;
import es.uco.ecoagent.llm.requirements.RequirementSpec;
import es.uco.ecoagent.llm.requirements.RequirementValidationException;
import es.uco.ecoagent.llm.requirements.RequirementsJson;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Contract tests for the JSON boundaries exposed by LLM-backed agents.
 */
public final class AgentOutputContractTest {
    @Test
    public void acceptsValidRequirementAndPatchContracts() {
        RequirementSpec requirements = RequirementsJson.parseAndValidate("{"
                + "\"projectName\":\"Tasks\",\"actors\":[\"USER\"],"
                + "\"requirements\":[{\"title\":\"Prioridad\","
                + "\"acceptanceCriteria\":[\"LOW MEDIUM HIGH\"],\"priority\":\"MUST\"}]}");
        PatchProposal patch = PatchProposalParser.parse("{"
                + "\"taskId\":\"TASK-001-1\",\"summary\":\"Crea enum\","
                + "\"unifiedDiff\":\"diff --git a/Priority.java b/Priority.java\","
                + "\"files\":[\"Priority.java\"]}");

        assertEquals("REQ-001", requirements.getRequirements().get(0).getId());
        assertEquals("TASK-001-1", patch.getTaskId());
    }

    @Test(expected = RequirementValidationException.class)
    public void rejectsPatchWithoutAffectedFilesContract() {
        PatchProposalParser.parse("{\"taskId\":\"TASK-001-1\",\"summary\":\"x\",\"unifiedDiff\":\"diff\"}");
    }

    @Test(expected = RequirementValidationException.class)
    public void rejectsRequirementWithInvalidPriorityContract() {
        RequirementsJson.parseAndValidate("{"
                + "\"projectName\":\"Tasks\",\"actors\":[\"USER\"],"
                + "\"requirements\":[{\"title\":\"Prioridad\","
                + "\"acceptanceCriteria\":[\"A\"],\"priority\":\"URGENT\"}]}");
    }
}
