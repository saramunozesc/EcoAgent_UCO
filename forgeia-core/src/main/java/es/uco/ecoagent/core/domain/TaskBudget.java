package es.uco.ecoagent.core.domain;

/**
 * Defines token, call and repair limits for a task execution.
 */
public final class TaskBudget {
    private final int maxContextTokens;
    private final int maxOutputTokens;
    private final int maxLlmCalls;
    private final int maxRepairAttempts;

    /**
     * Creates an immutable budget for controlling LLM usage.
     */
    public TaskBudget(int maxContextTokens, int maxOutputTokens, int maxLlmCalls, int maxRepairAttempts) {
        this.maxContextTokens = maxContextTokens;
        this.maxOutputTokens = maxOutputTokens;
        this.maxLlmCalls = maxLlmCalls;
        this.maxRepairAttempts = maxRepairAttempts;
    }

    /**
     * Returns the maximum context tokens allowed.
     */
    public int getMaxContextTokens() {
        return maxContextTokens;
    }

    /**
     * Returns the maximum output tokens allowed.
     */
    public int getMaxOutputTokens() {
        return maxOutputTokens;
    }

    /**
     * Returns the maximum number of LLM calls allowed.
     */
    public int getMaxLlmCalls() {
        return maxLlmCalls;
    }

    /**
     * Returns the maximum number of repair attempts allowed.
     */
    public int getMaxRepairAttempts() {
        return maxRepairAttempts;
    }
}

