package es.uco.ecoagent.orchestrator.run;

import es.uco.ecoagent.analysis.context.ContextSlice;
import es.uco.ecoagent.core.domain.TaskStatus;
import es.uco.ecoagent.core.orchestration.OrchestrationState;
import es.uco.ecoagent.core.planning.PlannedTask;
import es.uco.ecoagent.llm.code.PatchProposal;
import es.uco.ecoagent.llm.requirements.RequirementItem;
import es.uco.ecoagent.tools.verification.VerificationResult;

import java.nio.file.Path;

/**
 * Auditable outcome of one integrated ForgeIA run.
 */
public final class ForgeiaRunResult {
    private final String runId;
    private final RequirementItem requirement;
    private final PlannedTask task;
    private final ContextSlice contextSlice;
    private final PatchProposal finalProposal;
    private final VerificationResult verification;
    private final int repairAttempts;
    private final LlmUsageTotals usage;
    private final OrchestrationState orchestrationState;
    private final Path artifactPath;

    public ForgeiaRunResult(String runId, RequirementItem requirement, PlannedTask task,
            ContextSlice contextSlice, PatchProposal finalProposal, VerificationResult verification,
            int repairAttempts, LlmUsageTotals usage, OrchestrationState orchestrationState,
            Path artifactPath) {
        this.runId = runId;
        this.requirement = requirement;
        this.task = task;
        this.contextSlice = contextSlice;
        this.finalProposal = finalProposal;
        this.verification = verification;
        this.repairAttempts = repairAttempts;
        this.usage = usage;
        this.orchestrationState = orchestrationState;
        this.artifactPath = artifactPath;
    }

    public String getRunId() {
        return runId;
    }

    public RequirementItem getRequirement() {
        return requirement;
    }

    public PlannedTask getTask() {
        return task;
    }

    public ContextSlice getContextSlice() {
        return contextSlice;
    }

    public PatchProposal getFinalProposal() {
        return finalProposal;
    }

    public VerificationResult getVerification() {
        return verification;
    }

    public TaskStatus getStatus() {
        return verification.getStatus();
    }

    public int getRepairAttempts() {
        return repairAttempts;
    }

    public LlmUsageTotals getUsage() {
        return usage;
    }

    public OrchestrationState getOrchestrationState() {
        return orchestrationState;
    }

    public Path getArtifactPath() {
        return artifactPath;
    }
}
