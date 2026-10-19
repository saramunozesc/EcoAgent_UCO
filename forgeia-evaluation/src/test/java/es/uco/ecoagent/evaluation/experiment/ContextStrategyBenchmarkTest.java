package es.uco.ecoagent.evaluation.experiment;

import es.uco.ecoagent.evaluation.benchmark.BenchmarkDataset;
import es.uco.ecoagent.evaluation.benchmark.BenchmarkDatasetFactory;
import org.junit.Test;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * Verifies every benchmark task is compared under exactly A, B and C.
 */
public final class ContextStrategyBenchmarkTest {
    @Test
    public void runsEveryRequirementWithAllThreeContextStrategies() {
        BenchmarkDataset dataset = new BenchmarkDatasetFactory().starterDataset();
        ExperimentPlan plan = new ExperimentPlanner().plan(dataset.getCases());
        Map<String, Set<ContextStrategy>> strategiesByCase = new LinkedHashMap<String, Set<ContextStrategy>>();

        for (ExperimentRun run : plan.getRuns()) {
            String key = run.getExperimentCase().getApplicationId() + ":"
                    + run.getExperimentCase().getRequirementId();
            Set<ContextStrategy> strategies = strategiesByCase.get(key);
            if (strategies == null) {
                strategies = EnumSet.noneOf(ContextStrategy.class);
                strategiesByCase.put(key, strategies);
            }
            strategies.add(run.getStrategy());
        }

        assertEquals(dataset.getCases().size() * ContextStrategy.values().length, plan.getRuns().size());
        assertEquals(dataset.getCases().size(), strategiesByCase.size());
        for (Set<ContextStrategy> strategies : strategiesByCase.values()) {
            assertEquals(EnumSet.allOf(ContextStrategy.class), strategies);
        }
        assertSame(plan.getRuns().get(0).getExperimentCase(), plan.getRuns().get(1).getExperimentCase());
        assertSame(plan.getRuns().get(1).getExperimentCase(), plan.getRuns().get(2).getExperimentCase());
    }
}
