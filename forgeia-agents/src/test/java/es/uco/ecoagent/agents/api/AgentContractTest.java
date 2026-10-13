package es.uco.ecoagent.agents.api;

import es.uco.ecoagent.analysis.context.ContextSlice;
import es.uco.ecoagent.core.domain.TaskBudget;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Verifies the internal agent API contract.
 */
public final class AgentContractTest {
    /**
     * Executes an agent with project, task, budget, context and trace metadata.
     */
    @Test
    public void executesAgentWithContext() {
        ContextSlice slice = new ContextSlice("TASK-1", Collections.emptyList(), 0, "test", 0.0d);
        AgentContext context = new AgentContext("project-1", "TASK-1",
                new TaskBudget(8000, 2500, 4, 2), Collections.singletonList("readFile"), slice, "trace-1");
        Agent<String, String> agent = new Agent<String, String>() {
            public String execute(AgentContext context, String input) {
                return context.getProjectId() + ":" + context.getTaskId() + ":" + input;
            }
        };

        assertEquals("project-1:TASK-1:hello", agent.execute(context, "hello"));
        assertEquals("trace-1", context.getTraceId());
        assertEquals(8000, context.getTokenBudget().getMaxContextTokens());
    }

    /**
     * Keeps tool permissions immutable once the context is created.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void toolPermissionsAreImmutable() {
        ArrayList<String> permissions = new ArrayList<String>();
        permissions.add("readFile");
        AgentContext context = new AgentContext("project-1", "TASK-1",
                new TaskBudget(1, 1, 1, 1), permissions,
                new ContextSlice("TASK-1", Collections.emptyList(), 0, "test"), "trace");

        context.getToolPermissions().add("writeFile");
    }

    /**
     * Rejects unsafe structured output before workspace effects.
     */
    @Test
    public void validatesStructuredOutput() {
        StructuredOutputValidator<String> validator = new StructuredOutputValidator<String>() {
            public ValidationOutcome validate(String output) {
                return output.startsWith("{") ? ValidationOutcome.valid() : ValidationOutcome.invalid("JSON expected");
            }
        };

        assertFalse(validator.validate("free text").isValid());
        assertEquals("", validator.validate("{\"ok\":true}").getMessage());
    }
}
