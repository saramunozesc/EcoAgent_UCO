package es.uco.ecoagent.llm.repair;

import es.uco.ecoagent.llm.api.LlmClient;
import es.uco.ecoagent.llm.api.LlmRequest;
import es.uco.ecoagent.llm.api.LlmResponse;
import es.uco.ecoagent.llm.code.PatchProposal;
import es.uco.ecoagent.llm.code.PatchProposalParser;
import es.uco.ecoagent.tools.verification.NormalizedError;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Proposes repair patches from normalized errors and the previous diff.
 */
public final class RepairAgent {
    private static final String OUTPUT_SCHEMA = "{taskId:string,summary:string,unifiedDiff:string,files:string[]}";
    private final LlmClient llmClient;

    /**
     * Creates a repair agent backed by the provider-independent LLM API.
     */
    public RepairAgent(LlmClient llmClient) {
        this.llmClient = llmClient;
    }

    /**
     * Requests a repair patch without exposing full logs or the full project.
     */
    public PatchProposal proposeRepair(RepairInput input) {
        LlmResponse response = llmClient.complete(buildRequest(input));
        PatchProposal proposal = PatchProposalParser.parse(response.getContent());
        if (!proposal.getTaskId().equals(input.getTaskId())) {
            throw new IllegalStateException("Repair proposal taskId does not match requested task");
        }
        return proposal;
    }

    /**
     * Builds a repair prompt with only diff and normalized errors.
     */
    private LlmRequest buildRequest(RepairInput input) {
        Map<String, String> metadata = new LinkedHashMap<String, String>();
        metadata.put("agent", "RepairAgent");
        metadata.put("phase", "4");
        metadata.put("taskId", input.getTaskId());

        StringBuilder task = new StringBuilder();
        task.append("Corrige el fallo de verificacion para la tarea ").append(input.getTaskId()).append(".\n");
        task.append("Diff previo:\n").append(input.getPreviousDiff()).append("\n");
        task.append("Errores normalizados:\n");
        for (NormalizedError error : input.getErrors()) {
            task.append("- type=").append(error.getType())
                    .append(", file=").append(error.getFile())
                    .append(", line=").append(error.getLine())
                    .append(", message=").append(error.getMessage())
                    .append(", symbol=").append(error.getRelatedSymbol())
                    .append(", test=").append(error.getTestName())
                    .append("\n");
        }

        return new LlmRequest(
                "Eres un agente de reparacion. Responde solo JSON con un diff unificado. No ejecutes comandos.",
                task.toString(),
                "",
                OUTPUT_SCHEMA,
                2500,
                0.1d,
                metadata);
    }
}

