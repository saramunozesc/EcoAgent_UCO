package es.uco.ecoagent.evaluation.metrics;

import es.uco.ecoagent.core.domain.LlmUsage;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;

/**
 * Appends evaluation metrics as JSONL for reproducible experiments.
 */
public final class MetricsRecorder {
    private final Path metricsFile;
    private final Clock clock;

    /**
     * Creates a recorder that writes inside a workspace .forgeia directory.
     */
    public MetricsRecorder(Path workspaceRoot) {
        this(workspaceRoot.resolve(".forgeia").resolve("metrics.jsonl"), Clock.systemUTC());
    }

    /**
     * Creates a recorder with an injected clock for deterministic tests.
     */
    public MetricsRecorder(Path metricsFile, Clock clock) {
        this.metricsFile = metricsFile;
        this.clock = clock;
    }

    /**
     * Records input/output tokens, latency and cost for one LLM call.
     */
    public void recordLlmCall(String projectId, String requirementId, String taskId, String runId, LlmUsage usage) {
        Instant now = Instant.now(clock);
        record(new MetricRecord(MetricName.INPUT_TOKENS, MetricScope.LLM_CALL, projectId, requirementId, taskId, runId,
                BigDecimal.valueOf(usage.getInputTokens()), "tokens", now));
        record(new MetricRecord(MetricName.OUTPUT_TOKENS, MetricScope.LLM_CALL, projectId, requirementId, taskId, runId,
                BigDecimal.valueOf(usage.getOutputTokens()), "tokens", now));
        record(new MetricRecord(MetricName.LATENCY_MILLIS, MetricScope.LLM_CALL, projectId, requirementId, taskId, runId,
                BigDecimal.valueOf(usage.getLatencyMillis()), "ms", now));
        record(new MetricRecord(MetricName.ESTIMATED_COST, MetricScope.LLM_CALL, projectId, requirementId, taskId, runId,
                usage.getEstimatedCost(), "currency", now));
    }

    /**
     * Records a task-level metric such as context tokens or repair attempts.
     */
    public void recordTaskMetric(String projectId, String requirementId, String taskId, String runId,
            MetricName name, BigDecimal value, String unit) {
        record(new MetricRecord(name, MetricScope.TASK, projectId, requirementId, taskId, runId,
                value, unit, Instant.now(clock)));
    }

    /**
     * Records validation results for build and tests.
     */
    public void recordValidation(String projectId, String requirementId, String taskId, String runId,
            boolean buildSuccess, int testsPassed, int testsFailed) {
        Instant now = Instant.now(clock);
        record(new MetricRecord(MetricName.BUILD_SUCCESS, MetricScope.VALIDATION, projectId, requirementId, taskId,
                runId, BigDecimal.valueOf(buildSuccess ? 1 : 0), "boolean", now));
        record(new MetricRecord(MetricName.TESTS_PASSED, MetricScope.VALIDATION, projectId, requirementId, taskId,
                runId, BigDecimal.valueOf(testsPassed), "tests", now));
        record(new MetricRecord(MetricName.TESTS_FAILED, MetricScope.VALIDATION, projectId, requirementId, taskId,
                runId, BigDecimal.valueOf(testsFailed), "tests", now));
    }

    /**
     * Appends a prepared metric record.
     */
    public void record(MetricRecord record) {
        try {
            if (metricsFile.getParent() != null) {
                Files.createDirectories(metricsFile.getParent());
            }
            Files.write(metricsFile, (toJson(record) + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
                    Files.exists(metricsFile) ? java.nio.file.StandardOpenOption.APPEND : java.nio.file.StandardOpenOption.CREATE);
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot write evaluation metric", exception);
        }
    }

    /**
     * Returns the metrics file path.
     */
    public Path getMetricsFile() {
        return metricsFile;
    }

    /**
     * Serializes one metric as compact JSON.
     */
    private static String toJson(MetricRecord record) {
        return "{\"name\":\"" + record.getName() + "\",\"scope\":\"" + record.getScope()
                + "\",\"projectId\":\"" + escape(record.getProjectId())
                + "\",\"requirementId\":\"" + escape(record.getRequirementId())
                + "\",\"taskId\":\"" + escape(record.getTaskId())
                + "\",\"runId\":\"" + escape(record.getRunId())
                + "\",\"value\":" + record.getValue()
                + ",\"unit\":\"" + escape(record.getUnit())
                + "\",\"recordedAt\":\"" + record.getRecordedAt() + "\"}";
    }

    /**
     * Escapes nullable strings for the JSONL writer.
     */
    private static String escape(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
