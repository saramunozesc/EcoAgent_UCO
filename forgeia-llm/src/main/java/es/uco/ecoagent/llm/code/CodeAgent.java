package es.uco.ecoagent.llm.code;

import es.uco.ecoagent.llm.api.LlmClient;
import es.uco.ecoagent.llm.api.LlmRequest;
import es.uco.ecoagent.llm.api.LlmResponse;
import es.uco.ecoagent.llm.security.UntrustedProjectContent;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * First code agent that returns structured patches instead of writing files.
 */
public final class CodeAgent {
    private static final String OUTPUT_SCHEMA = "{taskId:string,summary:string,unifiedDiff:string,files:string[]}";
    private final LlmClient llmClient;

    /**
     * Creates a code agent backed by a provider-independent LLM client.
     */
    public CodeAgent(LlmClient llmClient) {
        this.llmClient = llmClient;
    }

    /**
     * Requests a patch proposal for one task and parses the structured response.
     */
    public PatchProposal propose(CodeTaskInput input) {
        LlmRequest request = buildRequest(input);
        LlmResponse response = llmClient.complete(request);
        PatchProposal proposal = PatchProposalParser.parse(response.getContent());
        if (!proposal.getTaskId().equals(input.getTask().getId())) {
            throw new IllegalStateException("Patch proposal taskId does not match requested task");
        }
        return proposal;
    }

    /**
     * Builds a prompt that separates architecture, task and selected context.
     */
    private LlmRequest buildRequest(CodeTaskInput input) {
        Map<String, String> metadata = new LinkedHashMap<String, String>();
        metadata.put("agent", "CodeAgent");
        metadata.put("phase", "3");
        metadata.put("taskId", input.getTask().getId());

        String task = "Devuelve una propuesta de parche para la tarea.\n"
                + "TaskId: " + input.getTask().getId() + "\n"
                + "Titulo: " + input.getTask().getTitle() + "\n"
                + "Tipo: " + input.getTask().getType() + "\n"
                + "Archivos objetivo probables: " + input.getTask().getTargetFiles() + "\n"
                + "Arquitectura aprobada:\n" + input.getArchitectureInstructions();
        return new LlmRequest(
                "Eres un agente de codigo. No escribas archivos. Responde solo JSON con un diff unificado. "
                        + UntrustedProjectContent.systemInstruction(),
                task,
                UntrustedProjectContent.wrap(input.getContextSlice()),
                OUTPUT_SCHEMA,
                input.getTask().getBudget().getMaxOutputTokens(),
                0.1d,
                metadata);
    }
}

