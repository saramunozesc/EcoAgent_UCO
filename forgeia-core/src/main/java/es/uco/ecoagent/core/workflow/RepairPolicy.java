package es.uco.ecoagent.core.workflow;

/**
 * Controls how many repair attempts are allowed for one task.
 */
public final class RepairPolicy {
    private final int maxAttempts;

    /**
     * Creates a repair policy with a hard attempt limit.
     */
    public RepairPolicy(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    /**
     * Returns whether another repair attempt can be made.
     */
    public boolean canAttempt(int previousAttempts) {
        return previousAttempts < maxAttempts;
    }

    /**
     * Returns the terminal status reason once attempts are exhausted.
     */
    public String exhaustedReason() {
        return "repair attempts exhausted after " + maxAttempts + " attempts";
    }
}

