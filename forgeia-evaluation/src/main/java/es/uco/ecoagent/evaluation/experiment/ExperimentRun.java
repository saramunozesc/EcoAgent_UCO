package es.uco.ecoagent.evaluation.experiment;

/**
 * Assigns one requirement case to one context strategy.
 */
public final class ExperimentRun {
    private final String id;
    private final ExperimentCase experimentCase;
    private final ContextStrategy strategy;

    /**
     * Creates an immutable experiment run descriptor.
     */
    public ExperimentRun(String id, ExperimentCase experimentCase, ContextStrategy strategy) {
        this.id = id;
        this.experimentCase = experimentCase;
        this.strategy = strategy;
    }

    /**
     * Returns the stable run identifier.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the benchmark case being executed.
     */
    public ExperimentCase getExperimentCase() {
        return experimentCase;
    }

    /**
     * Returns the context strategy assigned to this run.
     */
    public ContextStrategy getStrategy() {
        return strategy;
    }
}
