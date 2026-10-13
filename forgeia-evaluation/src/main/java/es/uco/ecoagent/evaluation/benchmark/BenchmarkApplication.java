package es.uco.ecoagent.evaluation.benchmark;

/**
 * Describes one controlled application template used in benchmarks.
 */
public final class BenchmarkApplication {
    private final String id;
    private final String name;
    private final String templatePath;

    /**
     * Creates an immutable benchmark application descriptor.
     */
    public BenchmarkApplication(String id, String name, String templatePath) {
        this.id = id;
        this.name = name;
        this.templatePath = templatePath;
    }

    /**
     * Returns the stable application identifier.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the human-readable application name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns where the deterministic template lives.
     */
    public String getTemplatePath() {
        return templatePath;
    }
}
