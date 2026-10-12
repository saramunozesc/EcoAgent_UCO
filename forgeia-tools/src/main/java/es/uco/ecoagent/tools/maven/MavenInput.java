package es.uco.ecoagent.tools.maven;

/**
 * Input for running an allowlisted Maven goal.
 */
public final class MavenInput {
    private final String goal;
    private final String module;

    /**
     * Creates a Maven request with an optional module.
     */
    public MavenInput(String goal, String module) {
        this.goal = goal;
        this.module = module;
    }

    /**
     * Returns the Maven goal or phase to execute.
     */
    public String getGoal() {
        return goal;
    }

    /**
     * Returns the optional module passed to -pl.
     */
    public String getModule() {
        return module;
    }
}

