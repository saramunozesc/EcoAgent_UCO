package es.uco.ecoagent.core.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Captures deterministic build and test validation after a patch.
 */
public final class ValidationRun {
    private final String buildStatus;
    private final int testsPassed;
    private final int testsFailed;
    private final List<String> errors;
    private final long durationMillis;

    /**
     * Creates an immutable validation result.
     */
    public ValidationRun(String buildStatus, int testsPassed, int testsFailed, List<String> errors, long durationMillis) {
        this.buildStatus = buildStatus;
        this.testsPassed = testsPassed;
        this.testsFailed = testsFailed;
        this.errors = Collections.unmodifiableList(new ArrayList<String>(errors));
        this.durationMillis = durationMillis;
    }

    /**
     * Returns PASS, FAIL or NEEDS_REVIEW for the build.
     */
    public String getBuildStatus() {
        return buildStatus;
    }

    /**
     * Returns the number of tests that passed.
     */
    public int getTestsPassed() {
        return testsPassed;
    }

    /**
     * Returns the number of tests that failed.
     */
    public int getTestsFailed() {
        return testsFailed;
    }

    /**
     * Returns normalized validation errors.
     */
    public List<String> getErrors() {
        return errors;
    }

    /**
     * Returns validation duration in milliseconds.
     */
    public long getDurationMillis() {
        return durationMillis;
    }
}
