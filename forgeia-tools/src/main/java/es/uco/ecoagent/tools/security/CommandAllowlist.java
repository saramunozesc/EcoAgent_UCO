package es.uco.ecoagent.tools.security;

import es.uco.ecoagent.tools.api.ToolException;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Restricts process execution to deterministic commands known by ForgeIA.
 */
public final class CommandAllowlist {
    private final Set<String> allowedCommands;

    /**
     * Creates an allowlist from command names accepted by ProcessBuilder.
     */
    public CommandAllowlist(Set<String> allowedCommands) {
        this.allowedCommands = Collections.unmodifiableSet(new HashSet<String>(allowedCommands));
    }

    /**
     * Returns the default commands needed by the phase 1 tools.
     */
    public static CommandAllowlist defaults() {
        return new CommandAllowlist(new HashSet<String>(Arrays.asList("git", "mvn", "mvn.cmd")));
    }

    /**
     * Fails if the command is not explicitly allowed.
     */
    public void requireAllowed(String command) {
        if (!allowedCommands.contains(command)) {
            throw new ToolException("Command is not allowed: " + command);
        }
    }
}

