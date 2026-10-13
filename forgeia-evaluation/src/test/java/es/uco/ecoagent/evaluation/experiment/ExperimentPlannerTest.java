package es.uco.ecoagent.evaluation.experiment;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Verifies the A/B/C context experiment plan.
 */
public final class ExperimentPlannerTest {
    /**
     * Creates three strategy runs for every requirement case.
     */
    @Test
    public void createsOneRunPerContextStrategy() {
        ExperimentCase experimentCase = new ExperimentCase("mathkids", "REQ-014", "Sistema de logros",
                Arrays.asList("Crea logro", "Evita duplicados"));

        ExperimentPlan plan = new ExperimentPlanner().plan(Arrays.asList(experimentCase));

        assertEquals(3, plan.getRuns().size());
        assertEquals(ContextStrategy.FULL_CONTEXT, plan.getRuns().get(0).getStrategy());
        assertEquals(ContextStrategy.BASIC_RETRIEVAL, plan.getRuns().get(1).getStrategy());
        assertEquals(ContextStrategy.FORGEIA_CONTEXT_COMPILER, plan.getRuns().get(2).getStrategy());
        assertTrue(plan.getRuns().get(2).getId().contains("forgeia_context_compiler"));
    }
}
