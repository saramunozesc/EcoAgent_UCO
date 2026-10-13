package es.uco.ecoagent.evaluation.experiment;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds deterministic A/B/C experiment plans from benchmark cases.
 */
public final class ExperimentPlanner {
    /**
     * Creates one run per case and context strategy.
     */
    public ExperimentPlan plan(List<ExperimentCase> cases) {
        List<ExperimentRun> runs = new ArrayList<ExperimentRun>();
        for (ExperimentCase experimentCase : cases) {
            for (ContextStrategy strategy : ContextStrategy.values()) {
                String id = experimentCase.getApplicationId() + "-" + experimentCase.getRequirementId() + "-"
                        + strategy.name().toLowerCase();
                runs.add(new ExperimentRun(id, experimentCase, strategy));
            }
        }
        return new ExperimentPlan(runs);
    }
}
