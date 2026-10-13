package es.uco.ecoagent.evaluation.experiment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Contains the full set of runs needed for an A/B/C context experiment.
 */
public final class ExperimentPlan {
    private final List<ExperimentRun> runs;

    /**
     * Creates an immutable experiment plan.
     */
    public ExperimentPlan(List<ExperimentRun> runs) {
        this.runs = Collections.unmodifiableList(new ArrayList<ExperimentRun>(runs));
    }

    /**
     * Returns every strategy/case execution to run.
     */
    public List<ExperimentRun> getRuns() {
        return runs;
    }
}
