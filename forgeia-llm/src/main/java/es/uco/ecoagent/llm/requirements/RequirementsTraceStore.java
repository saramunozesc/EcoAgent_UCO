package es.uco.ecoagent.llm.requirements;

import es.uco.ecoagent.llm.api.LlmRequest;
import es.uco.ecoagent.llm.api.LlmResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Persists generated requirements and trace data for reproducibility.
 */
public final class RequirementsTraceStore {
    /**
     * Writes the validated spec and one JSONL trace entry to disk.
     */
    public RequirementGenerationResult persist(Path outputDirectory, LlmRequest request, LlmResponse response, RequirementSpec spec) {
        try {
            Files.createDirectories(outputDirectory);
            Path specFile = outputDirectory.resolve("requirements.json");
            Path traceFile = outputDirectory.resolve("requirements-trace.jsonl");
            Files.write(specFile, RequirementsJson.toJson(spec).getBytes(StandardCharsets.UTF_8));
            Files.write(traceFile, traceEntry(request, response).getBytes(StandardCharsets.UTF_8));
            return new RequirementGenerationResult(spec, specFile, traceFile);
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot persist requirements output", exception);
        }
    }

    /**
     * Builds a compact JSONL trace with prompt, model, tokens and response.
     */
    private String traceEntry(LlmRequest request, LlmResponse response) {
        return "{"
                + "\"agent\":\"RequirementsAgent\","
                + "\"provider\":\"" + RequirementsJson.escape(response.getProvider()) + "\","
                + "\"model\":\"" + RequirementsJson.escape(response.getModel()) + "\","
                + "\"inputTokens\":" + response.getInputTokens() + ","
                + "\"outputTokens\":" + response.getOutputTokens() + ","
                + "\"latencyMillis\":" + response.getLatencyMillis() + ","
                + "\"systemInstructions\":\"" + RequirementsJson.escape(request.getSystemInstructions()) + "\","
                + "\"task\":\"" + RequirementsJson.escape(request.getTask()) + "\","
                + "\"outputSchema\":\"" + RequirementsJson.escape(request.getOutputSchema()) + "\","
                + "\"response\":\"" + RequirementsJson.escape(response.getContent()) + "\""
                + "}\n";
    }
}

