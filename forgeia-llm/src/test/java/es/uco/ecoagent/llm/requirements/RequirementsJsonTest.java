package es.uco.ecoagent.llm.requirements;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Verifies strict parsing and deterministic ID assignment.
 */
public class RequirementsJsonTest {
    /**
     * Ensures IDs from the LLM are ignored and replaced deterministically.
     */
    @Test
    public void assignsDeterministicRequirementIds() {
        RequirementSpec spec = RequirementsJson.parseAndValidate("{"
                + "\"projectName\":\"Demo\","
                + "\"actors\":[\"USER\"],"
                + "\"requirements\":["
                + "{\"id\":\"abc\",\"title\":\"Uno\",\"acceptanceCriteria\":[\"A\"],\"priority\":\"MUST\"},"
                + "{\"id\":\"xyz\",\"title\":\"Dos\",\"acceptanceCriteria\":[\"B\"],\"priority\":\"SHOULD\"}"
                + "]"
                + "}");

        assertEquals("REQ-001", spec.getRequirements().get(0).getId());
        assertEquals("REQ-002", spec.getRequirements().get(1).getId());
    }

    /**
     * Ensures malformed schemas are rejected before persistence.
     */
    @Test(expected = RequirementValidationException.class)
    public void rejectsMissingRequirements() {
        RequirementsJson.parseAndValidate("{\"projectName\":\"Demo\",\"actors\":[\"USER\"]}");
    }
}

