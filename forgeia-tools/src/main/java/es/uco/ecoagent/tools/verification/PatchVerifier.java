package es.uco.ecoagent.tools.verification;

import es.uco.ecoagent.core.domain.TaskStatus;
import es.uco.ecoagent.tools.api.ToolExecutionContext;
import es.uco.ecoagent.tools.command.CommandResult;
import es.uco.ecoagent.tools.maven.TestInput;
import es.uco.ecoagent.tools.maven.TestTool;
import es.uco.ecoagent.tools.patch.ApplyPatchInput;
import es.uco.ecoagent.tools.patch.ApplyPatchResult;
import es.uco.ecoagent.tools.patch.ApplyPatchTool;

import java.util.Collections;
import java.util.List;

/**
 * Applies a patch and lets Maven decide whether the result is valid.
 */
public final class PatchVerifier {
    private final ApplyPatchTool applyPatchTool;
    private final TestTool testTool;
    private final ErrorNormalizer errorNormalizer;

    /**
     * Creates a verifier with deterministic patch, test and normalization tools.
     */
    public PatchVerifier(ApplyPatchTool applyPatchTool, TestTool testTool, ErrorNormalizer errorNormalizer) {
        this.applyPatchTool = applyPatchTool;
        this.testTool = testTool;
        this.errorNormalizer = errorNormalizer;
    }

    /**
     * Applies the diff and runs Maven tests, returning DONE or FAILED.
     */
    public VerificationResult verify(ToolExecutionContext context, String unifiedDiff, String module, String testSelector) {
        ApplyPatchResult patchResult = applyPatchTool.execute(context, new ApplyPatchInput(unifiedDiff));
        if (patchResult.getExitCode() != 0) {
            List<NormalizedError> errors = errorNormalizer.normalize(patchResult.getOutput());
            return new VerificationResult(TaskStatus.FAILED, patchResult.getExitCode(), -1, errors, patchResult.getOutput());
        }

        CommandResult testResult = testTool.execute(context, new TestInput(testSelector, module));
        if (testResult.getExitCode() == 0) {
            return new VerificationResult(TaskStatus.DONE, patchResult.getExitCode(), testResult.getExitCode(), Collections.<NormalizedError>emptyList(), testResult.getOutput());
        }

        List<NormalizedError> errors = errorNormalizer.normalize(testResult.getOutput());
        return new VerificationResult(TaskStatus.FAILED, patchResult.getExitCode(), testResult.getExitCode(), errors, testResult.getOutput());
    }
}

