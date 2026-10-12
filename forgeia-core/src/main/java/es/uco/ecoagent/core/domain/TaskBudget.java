package es.uco.ecoagent.core.domain;

public final class TaskBudget {
    private final int maxContextTokens;
    private final int maxOutputTokens;
    private final int maxLlmCalls;
    private final int maxRepairAttempts;

    public TaskBudget(int maxContextTokens, int maxOutputTokens, int maxLlmCalls, int maxRepairAttempts) {
        this.maxContextTokens = maxContextTokens;
        this.maxOutputTokens = maxOutputTokens;
        this.maxLlmCalls = maxLlmCalls;
        this.maxRepairAttempts = maxRepairAttempts;
    }

    public int getMaxContextTokens() {
        return maxContextTokens;
    }

    public int getMaxOutputTokens() {
        return maxOutputTokens;
    }

    public int getMaxLlmCalls() {
        return maxLlmCalls;
    }

    public int getMaxRepairAttempts() {
        return maxRepairAttempts;
    }
}

