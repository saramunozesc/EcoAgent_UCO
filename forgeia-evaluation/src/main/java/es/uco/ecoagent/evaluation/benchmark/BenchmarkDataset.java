package es.uco.ecoagent.evaluation.benchmark;

import es.uco.ecoagent.evaluation.experiment.ExperimentCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Groups controlled applications and requirements for experimental evaluation.
 */
public final class BenchmarkDataset {
    private final List<BenchmarkApplication> applications;
    private final List<ExperimentCase> cases;

    /**
     * Creates an immutable benchmark dataset.
     */
    public BenchmarkDataset(List<BenchmarkApplication> applications, List<ExperimentCase> cases) {
        this.applications = Collections.unmodifiableList(new ArrayList<BenchmarkApplication>(applications));
        this.cases = Collections.unmodifiableList(new ArrayList<ExperimentCase>(cases));
    }

    /**
     * Returns benchmark applications.
     */
    public List<BenchmarkApplication> getApplications() {
        return applications;
    }

    /**
     * Returns requirement cases with acceptance criteria.
     */
    public List<ExperimentCase> getCases() {
        return cases;
    }
}
