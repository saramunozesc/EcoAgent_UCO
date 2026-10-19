package es.uco.ecoagent.orchestrator.run;

import es.uco.ecoagent.analysis.context.ContextCompiler;
import es.uco.ecoagent.analysis.context.ContextItem;
import es.uco.ecoagent.analysis.context.ContextSlice;
import es.uco.ecoagent.core.agents.AgentRole;
import es.uco.ecoagent.core.blackboard.Blackboard;
import es.uco.ecoagent.core.blackboard.BlackboardArtifact;
import es.uco.ecoagent.core.domain.Requirement;
import es.uco.ecoagent.core.domain.RequirementStatus;
import es.uco.ecoagent.core.domain.TaskBudget;
import es.uco.ecoagent.core.domain.TaskStatus;
import es.uco.ecoagent.core.logging.RunId;
import es.uco.ecoagent.core.orchestration.MultiAgentOrchestrator;
import es.uco.ecoagent.core.planning.PlannedTask;
import es.uco.ecoagent.core.planning.RequirementPlan;
import es.uco.ecoagent.core.planning.RequirementPlanner;
import es.uco.ecoagent.llm.api.LlmClient;
import es.uco.ecoagent.llm.api.LlmRequest;
import es.uco.ecoagent.llm.api.LlmResponse;
import es.uco.ecoagent.llm.code.CodeAgent;
import es.uco.ecoagent.llm.code.CodeTaskInput;
import es.uco.ecoagent.llm.code.PatchProposal;
import es.uco.ecoagent.llm.repair.RepairAgent;
import es.uco.ecoagent.llm.repair.RepairInput;
import es.uco.ecoagent.llm.requirements.RequirementGenerationResult;
import es.uco.ecoagent.llm.requirements.RequirementItem;
import es.uco.ecoagent.llm.requirements.RequirementsAgent;
import es.uco.ecoagent.llm.requirements.RequirementsTraceStore;
import es.uco.ecoagent.tools.api.ToolExecutionContext;
import es.uco.ecoagent.tools.maven.TestTool;
import es.uco.ecoagent.tools.patch.ApplyPatchTool;
import es.uco.ecoagent.tools.verification.ErrorNormalizer;
import es.uco.ecoagent.tools.verification.PatchVerifier;
import es.uco.ecoagent.tools.verification.VerificationResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Connects requirements, planning, context, code generation and verification.
 */
public final class ForgeiaRunService {
    private final LlmClient llmClient;
    private final TaskBudget defaultBudget;

    public ForgeiaRunService(LlmClient llmClient, TaskBudget defaultBudget) {
        if (llmClient == null || defaultBudget == null) {
            throw new IllegalArgumentException("LLM client and default budget are required");
        }
        this.llmClient = llmClient;
        this.defaultBudget = defaultBudget;
    }

    /**
     * Executes one selected task and persists its trace under .forgeia/runs.
     */
    public ForgeiaRunResult execute(ForgeiaRunRequest request) {
        long startedAt = System.currentTimeMillis();
        String runId = RunId.create();
        Path runDirectory = request.getWorkspace().resolve(".forgeia").resolve("runs").resolve(runId);
        RecordingLlmClient recordingClient = new RecordingLlmClient(llmClient, defaultBudget.getMaxLlmCalls());
        Blackboard blackboard = new Blackboard(request.getWorkspace());
        MultiAgentOrchestrator orchestrator = new MultiAgentOrchestrator(blackboard);

        RequirementGenerationResult generated = new RequirementsAgent(recordingClient, new RequirementsTraceStore())
                .generate(request.getIdea(), runDirectory);
        RequirementItem item = select(generated, request.getRequirementIndex());
        orchestrator.acceptArtifact(AgentRole.REQUIREMENTS, BlackboardArtifact.REQUIREMENTS,
                read(generated.getSpecFile()));
        orchestrator.acceptArtifact(AgentRole.ARCHITECT, BlackboardArtifact.ARCHITECTURE,
                architectureJson(request.getArchitectureInstructions()));

        Requirement requirement = new Requirement(item.getId(), item.getTitle(), request.getIdea(),
                item.getAcceptanceCriteria(), item.getPriority(), RequirementStatus.APPROVED);
        RequirementPlan plan = new RequirementPlanner(defaultBudget).plan(requirement);
        PlannedTask task = select(plan, request.getTaskIndex());
        orchestrator.markTasksReady(taskJson(task));

        ContextSlice context = new ContextCompiler().compile(request.getWorkspace(), task);
        PatchProposal proposal = new CodeAgent(recordingClient).propose(new CodeTaskInput(
                task, request.getArchitectureInstructions(), formatContext(context)));
        orchestrator.acceptArtifact(AgentRole.BACKEND, BlackboardArtifact.DECISIONS, proposalJson(proposal));

        PatchVerifier verifier = new PatchVerifier(new ApplyPatchTool(), new TestTool(), new ErrorNormalizer());
        ToolExecutionContext toolContext = new ToolExecutionContext(request.getWorkspace());
        VerificationResult verification;
        int repairAttempts = 0;
        PatchProposal finalProposal = proposal;

        while (true) {
            orchestrator.acceptArtifact(AgentRole.REVIEWER, BlackboardArtifact.TASK_STATUS,
                    "{\"taskId\":\"" + escape(task.getId()) + "\",\"status\":\"VERIFYING\"}");
            verification = verifier.verify(toolContext, finalProposal.getUnifiedDiff(), null, null);
            if (verification.getStatus() == TaskStatus.DONE) {
                orchestrator.advanceVerification(true, "maven-test");
                break;
            }
            if (repairAttempts >= task.getBudget().getMaxRepairAttempts()) {
                orchestrator.advanceVerification(false, "repair-budget-exhausted");
                break;
            }
            repairAttempts++;
            finalProposal = new RepairAgent(recordingClient).proposeRepair(new RepairInput(
                    task.getId(), finalProposal.getUnifiedDiff(), verification.getErrors()));
            orchestrator.acceptArtifact(AgentRole.REPAIR, BlackboardArtifact.DECISIONS,
                    proposalJson(finalProposal));
        }

        LlmUsageTotals usage = recordingClient.totals();
        long durationMillis = System.currentTimeMillis() - startedAt;
        String metrics = metricsJson(runId, task, context, verification, repairAttempts, usage, durationMillis);
        blackboard.appendMetric(metrics);
        Path artifactPath = persistRun(runDirectory, metrics, item, finalProposal, context,
                orchestrator.getState().name());
        return new ForgeiaRunResult(runId, item, task, context, finalProposal, verification,
                repairAttempts, usage, orchestrator.getState(), artifactPath);
    }

    private static RequirementItem select(RequirementGenerationResult result, int index) {
        if (index >= result.getSpec().getRequirements().size()) {
            throw new IllegalArgumentException("Generated requirement index is out of range: " + index);
        }
        return result.getSpec().getRequirements().get(index);
    }

    private static PlannedTask select(RequirementPlan plan, int index) {
        if (index >= plan.getTasks().size()) {
            throw new IllegalArgumentException("Planned task index is out of range: " + index);
        }
        return plan.getTasks().get(index);
    }

    private static String formatContext(ContextSlice slice) {
        StringBuilder output = new StringBuilder();
        output.append("strategy=").append(slice.getStrategy())
                .append(" tokens=").append(slice.getTokenCount()).append("\n");
        for (ContextItem item : slice.getItems()) {
            output.append("\n--- ").append(item.getPath()).append(" [")
                    .append(item.getLevel()).append("] ---\n").append(item.getContent());
        }
        return output.toString();
    }

    private static String architectureJson(String instructions) {
        return "{\"instructions\":\"" + escape(instructions) + "\"}";
    }

    private static String taskJson(PlannedTask task) {
        return "{\"taskId\":\"" + escape(task.getId()) + "\",\"title\":\""
                + escape(task.getTitle()) + "\",\"status\":\"READY\"}";
    }

    private static String proposalJson(PatchProposal proposal) {
        return "{\"taskId\":\"" + escape(proposal.getTaskId()) + "\",\"summary\":\""
                + escape(proposal.getSummary()) + "\",\"diff\":\""
                + escape(proposal.getUnifiedDiff()) + "\"}";
    }

    private static String metricsJson(String runId, PlannedTask task, ContextSlice context,
            VerificationResult verification, int repairAttempts, LlmUsageTotals usage, long durationMillis) {
        return "{\"runId\":\"" + escape(runId) + "\",\"taskId\":\"" + escape(task.getId())
                + "\",\"status\":\"" + verification.getStatus() + "\",\"contextTokens\":"
                + context.getTokenCount() + ",\"llmCalls\":" + usage.getCalls()
                + ",\"inputTokens\":" + usage.getInputTokens() + ",\"outputTokens\":"
                + usage.getOutputTokens() + ",\"llmLatencyMillis\":" + usage.getLatencyMillis()
                + ",\"repairAttempts\":" + repairAttempts + ",\"durationMillis\":"
                + durationMillis + "}";
    }

    private static Path persistRun(Path runDirectory, String metrics, RequirementItem requirement,
            PatchProposal proposal, ContextSlice context, String orchestrationState) {
        try {
            Files.createDirectories(runDirectory);
            Files.write(runDirectory.resolve("context.txt"),
                    formatContext(context).getBytes(StandardCharsets.UTF_8));
            Files.write(runDirectory.resolve("final.patch"),
                    proposal.getUnifiedDiff().getBytes(StandardCharsets.UTF_8));
            Path output = runDirectory.resolve("run-result.json");
            String json = "{\n  \"requirementId\": \"" + escape(requirement.getId())
                    + "\",\n  \"taskId\": \"" + escape(proposal.getTaskId())
                    + "\",\n  \"orchestrationState\": \"" + escape(orchestrationState)
                    + "\",\n  \"contextArtifact\": \"context.txt\""
                    + ",\n  \"patchArtifact\": \"final.patch\""
                    + ",\n  \"metrics\": " + metrics + "\n}\n";
            Files.write(output, json.getBytes(StandardCharsets.UTF_8));
            return output;
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot persist run result", exception);
        }
    }

    private static String read(Path path) {
        try {
            return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot read generated artifact: " + path, exception);
        }
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\r", "\\r").replace("\n", "\\n");
    }

    private static final class RecordingLlmClient implements LlmClient {
        private final LlmClient delegate;
        private final int maxCalls;
        private final List<LlmResponse> responses = new ArrayList<LlmResponse>();

        private RecordingLlmClient(LlmClient delegate, int maxCalls) {
            this.delegate = delegate;
            this.maxCalls = maxCalls;
        }

        public LlmResponse complete(LlmRequest request) {
            if (responses.size() >= maxCalls) {
                throw new IllegalStateException("LLM call budget exhausted: " + maxCalls);
            }
            LlmResponse response = delegate.complete(request);
            responses.add(response);
            return response;
        }

        private LlmUsageTotals totals() {
            int inputTokens = 0;
            int outputTokens = 0;
            long latencyMillis = 0L;
            for (LlmResponse response : responses) {
                inputTokens += response.getInputTokens();
                outputTokens += response.getOutputTokens();
                latencyMillis += response.getLatencyMillis();
            }
            return new LlmUsageTotals(responses.size(), inputTokens, outputTokens, latencyMillis);
        }
    }
}
