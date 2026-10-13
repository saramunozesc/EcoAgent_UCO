package es.uco.ecoagent.llm.requirements;

import es.uco.ecoagent.llm.api.LlmClient;
import es.uco.ecoagent.llm.api.LlmRequest;
import es.uco.ecoagent.llm.api.LlmResponse;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Converts a product idea into validated requirements JSON.
 */
public final class RequirementsAgent {
    private static final String OUTPUT_SCHEMA = "{projectName:string,actors:string[],requirements:[{title:string,acceptanceCriteria:string[],priority:MUST|SHOULD|COULD}]}";
    private final LlmClient llmClient;
    private final RequirementsTraceStore traceStore;

    /**
     * Creates an agent using a provider-independent LLM client.
     */
    public RequirementsAgent(LlmClient llmClient, RequirementsTraceStore traceStore) {
        this.llmClient = llmClient;
        this.traceStore = traceStore;
    }

    /**
     * Generates, validates, persists and traces requirements for one idea.
     */
    public RequirementGenerationResult generate(String idea, Path outputDirectory) {
        LlmRequest request = buildRequest(idea, false, null);
        LlmResponse response = llmClient.complete(request);
        RequirementSpec spec;
        LlmRequest finalRequest = request;
        LlmResponse finalResponse = response;
        try {
            spec = RequirementsJson.parseAndValidate(response.getContent());
        } catch (RuntimeException firstFailure) {
            finalRequest = buildRequest(idea, true, response.getContent());
            finalResponse = llmClient.complete(finalRequest);
            spec = RequirementsJson.parseAndValidate(finalResponse.getContent());
        }

        return traceStore.persist(outputDirectory, finalRequest, finalResponse, spec);
    }

    /**
     * Builds the model request with schema and reproducibility metadata.
     */
    private LlmRequest buildRequest(String idea, boolean repair, String invalidJson) {
        Map<String, String> metadata = new LinkedHashMap<String, String>();
        metadata.put("agent", "RequirementsAgent");
        metadata.put("phase", "2");
        metadata.put("repair", String.valueOf(repair));

        String task = repair
                ? "Repara el JSON para que cumpla el esquema. No generes codigo.\nIdea:\n" + idea + "\nJSON invalido:\n" + invalidJson
                : "Convierte esta idea en requisitos JSON validables. No generes codigo.\nIdea:\n" + idea;
        return new LlmRequest(
                "Eres un agente de requisitos. Responde solo JSON valido segun el esquema solicitado.",
                task,
                "",
                OUTPUT_SCHEMA,
                2500,
                0.1d,
                metadata);
    }
}

