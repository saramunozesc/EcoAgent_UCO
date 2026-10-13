package es.uco.ecoagent.evaluation.metrics;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Stores one numeric metric with trace identifiers for later analysis.
 */
public final class MetricRecord {
    private final MetricName name;
    private final MetricScope scope;
    private final String projectId;
    private final String requirementId;
    private final String taskId;
    private final String runId;
    private final BigDecimal value;
    private final String unit;
    private final Instant recordedAt;

    /**
     * Creates an immutable metric record.
     */
    public MetricRecord(MetricName name, MetricScope scope, String projectId, String requirementId, String taskId,
            String runId, BigDecimal value, String unit, Instant recordedAt) {
        this.name = name;
        this.scope = scope;
        this.projectId = projectId;
        this.requirementId = requirementId;
        this.taskId = taskId;
        this.runId = runId;
        this.value = value;
        this.unit = unit;
        this.recordedAt = recordedAt;
    }

    /**
     * Returns the metric identifier.
     */
    public MetricName getName() {
        return name;
    }

    /**
     * Returns the level where the metric was measured.
     */
    public MetricScope getScope() {
        return scope;
    }

    /**
     * Returns the project identifier.
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * Returns the requirement identifier when available.
     */
    public String getRequirementId() {
        return requirementId;
    }

    /**
     * Returns the task identifier when available.
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * Returns the execution identifier.
     */
    public String getRunId() {
        return runId;
    }

    /**
     * Returns the numeric metric value.
     */
    public BigDecimal getValue() {
        return value;
    }

    /**
     * Returns the metric unit.
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Returns when the metric was recorded.
     */
    public Instant getRecordedAt() {
        return recordedAt;
    }
}
