package es.uco.ecoagent.evaluation.metrics;

import java.math.BigDecimal;
import java.util.List;

/**
 * Aggregates raw metric records into project-level quality indicators.
 */
public final class MetricsAggregator {
    /**
     * Summarizes phase 9 metrics for one project.
     */
    public ProjectMetricsSummary summarize(List<MetricRecord> records) {
        int llmCalls = 0;
        int inputTokens = 0;
        int outputTokens = 0;
        int contextTokens = 0;
        int testsPassed = 0;
        int testsFailed = 0;
        int repairAttempts = 0;
        int requirementsCompleted = 0;
        int buildChecks = 0;
        int buildSuccesses = 0;
        BigDecimal cost = BigDecimal.ZERO;

        for (MetricRecord record : records) {
            switch (record.getName()) {
                case INPUT_TOKENS:
                    inputTokens += record.getValue().intValue();
                    llmCalls++;
                    break;
                case OUTPUT_TOKENS:
                    outputTokens += record.getValue().intValue();
                    break;
                case CONTEXT_TOKENS:
                    contextTokens += record.getValue().intValue();
                    break;
                case TESTS_PASSED:
                    testsPassed += record.getValue().intValue();
                    break;
                case TESTS_FAILED:
                    testsFailed += record.getValue().intValue();
                    break;
                case REPAIR_ATTEMPTS:
                    repairAttempts += record.getValue().intValue();
                    break;
                case REQUIREMENTS_COMPLETED:
                    requirementsCompleted += record.getValue().intValue();
                    break;
                case ESTIMATED_COST:
                    cost = cost.add(record.getValue());
                    break;
                case BUILD_SUCCESS:
                    buildChecks++;
                    buildSuccesses += record.getValue().intValue();
                    break;
                default:
                    break;
            }
        }
        double buildRate = buildChecks == 0 ? 0.0d : (double) buildSuccesses / (double) buildChecks;
        return new ProjectMetricsSummary(llmCalls, inputTokens, outputTokens, contextTokens, testsPassed,
                testsFailed, repairAttempts, requirementsCompleted, cost, buildRate);
    }
}
