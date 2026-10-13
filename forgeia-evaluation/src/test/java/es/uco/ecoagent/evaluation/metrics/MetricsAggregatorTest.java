package es.uco.ecoagent.evaluation.metrics;

import org.junit.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Verifies aggregation of raw evaluation metrics.
 */
public final class MetricsAggregatorTest {
    /**
     * Aggregates tokens, tests, repairs and build success rate.
     */
    @Test
    public void summarizesProjectMetrics() {
        Instant now = Instant.parse("2026-10-13T12:00:00Z");
        ProjectMetricsSummary summary = new MetricsAggregator().summarize(Arrays.asList(
                metric(MetricName.INPUT_TOKENS, MetricScope.LLM_CALL, "100"),
                metric(MetricName.OUTPUT_TOKENS, MetricScope.LLM_CALL, "40"),
                metric(MetricName.CONTEXT_TOKENS, MetricScope.TASK, "900"),
                metric(MetricName.TESTS_PASSED, MetricScope.VALIDATION, "18"),
                metric(MetricName.TESTS_FAILED, MetricScope.VALIDATION, "0"),
                metric(MetricName.REPAIR_ATTEMPTS, MetricScope.TASK, "1"),
                metric(MetricName.REQUIREMENTS_COMPLETED, MetricScope.PROJECT, "3"),
                metric(MetricName.ESTIMATED_COST, MetricScope.LLM_CALL, "0.025"),
                new MetricRecord(MetricName.BUILD_SUCCESS, MetricScope.VALIDATION, "p", "r", "t", "run",
                        BigDecimal.ONE, "boolean", now),
                new MetricRecord(MetricName.BUILD_SUCCESS, MetricScope.VALIDATION, "p", "r", "t2", "run",
                        BigDecimal.ZERO, "boolean", now)));

        assertEquals(1, summary.getLlmCalls());
        assertEquals(100, summary.getInputTokens());
        assertEquals(40, summary.getOutputTokens());
        assertEquals(900, summary.getContextTokens());
        assertEquals(18, summary.getTestsPassed());
        assertEquals(1, summary.getRepairAttempts());
        assertEquals(new BigDecimal("0.025"), summary.getEstimatedCost());
        assertEquals(0.5d, summary.getBuildSuccessRate(), 0.0001d);
    }

    /**
     * Creates one metric record for aggregation tests.
     */
    private static MetricRecord metric(MetricName name, MetricScope scope, String value) {
        return new MetricRecord(name, scope, "p", "r", "t", "run", new BigDecimal(value), "unit",
                Instant.parse("2026-10-13T12:00:00Z"));
    }
}
