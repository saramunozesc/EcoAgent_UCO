package es.uco.ecoagent.llm.requirements;

import java.nio.file.Path;

/**
 * Result of turning one idea into persisted requirements.
 */
public final class RequirementGenerationResult {
    private final RequirementSpec spec;
    private final Path specFile;
    private final Path traceFile;

    /**
     * Stores the validated spec and persistence locations.
     */
    public RequirementGenerationResult(RequirementSpec spec, Path specFile, Path traceFile) {
        this.spec = spec;
        this.specFile = specFile;
        this.traceFile = traceFile;
    }

    /**
     * Returns the validated requirement specification.
     */
    public RequirementSpec getSpec() {
        return spec;
    }

    /**
     * Returns the JSON file containing the persisted requirements.
     */
    public Path getSpecFile() {
        return specFile;
    }

    /**
     * Returns the JSONL trace file containing prompts and token usage.
     */
    public Path getTraceFile() {
        return traceFile;
    }
}

