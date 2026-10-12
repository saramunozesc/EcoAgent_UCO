package es.uco.ecoagent.tools.command;

/**
 * Contains a bounded summary of a deterministic process execution.
 */
public final class CommandResult {
    private final int exitCode;
    private final String output;
    private final boolean timedOut;

    /**
     * Creates a structured command result with summarized output.
     */
    public CommandResult(int exitCode, String output, boolean timedOut) {
        this.exitCode = exitCode;
        this.output = output;
        this.timedOut = timedOut;
    }

    /**
     * Returns the process exit code or -1 when timed out.
     */
    public int getExitCode() {
        return exitCode;
    }

    /**
     * Returns the bounded stdout and stderr summary.
     */
    public String getOutput() {
        return output;
    }

    /**
     * Returns whether the command exceeded its timeout.
     */
    public boolean isTimedOut() {
        return timedOut;
    }
}

