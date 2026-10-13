package es.uco.ecoagent.tools.verification;

import es.uco.ecoagent.core.domain.TaskStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Structured result of patch application and Maven verification.
 */
public final class VerificationResult {
    private final TaskStatus status;
    private final int patchExitCode;
    private final int testExitCode;
    private final List<NormalizedError> errors;
    private final String output;

    /**
     * Creates an immutable verification result.
     */
    public VerificationResult(TaskStatus status, int patchExitCode, int testExitCode, List<NormalizedError> errors, String output) {
        this.status = status;
        this.patchExitCode = patchExitCode;
        this.testExitCode = testExitCode;
        this.errors = Collections.unmodifiableList(new ArrayList<NormalizedError>(errors));
        this.output = output;
    }

    /**
     * Returns DONE when Maven passes or FAILED when repair is needed.
     */
    public TaskStatus getStatus() {
        return status;
    }

    /**
     * Returns the patch command exit code.
     */
    public int getPatchExitCode() {
        return patchExitCode;
    }

    /**
     * Returns the Maven test exit code.
     */
    public int getTestExitCode() {
        return testExitCode;
    }

    /**
     * Returns normalized errors suitable for repair prompts.
     */
    public List<NormalizedError> getErrors() {
        return errors;
    }

    /**
     * Returns compact command output for auditing.
     */
    public String getOutput() {
        return output;
    }
}

