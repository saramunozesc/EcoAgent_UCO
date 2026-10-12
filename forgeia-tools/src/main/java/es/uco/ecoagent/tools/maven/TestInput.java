package es.uco.ecoagent.tools.maven;

/**
 * Input for running tests with an optional selector.
 */
public final class TestInput {
    private final String selector;
    private final String module;

    /**
     * Creates a test request with optional test selector and module.
     */
    public TestInput(String selector, String module) {
        this.selector = selector;
        this.module = module;
    }

    /**
     * Returns the optional Maven -Dtest selector.
     */
    public String getSelector() {
        return selector;
    }

    /**
     * Returns the optional module passed to -pl.
     */
    public String getModule() {
        return module;
    }
}

