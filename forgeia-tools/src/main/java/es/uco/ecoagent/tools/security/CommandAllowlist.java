package es.uco.ecoagent.tools.security;

import es.uco.ecoagent.tools.api.ToolException;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Restricts process execution to deterministic commands known by ForgeIA.
 */
public final class CommandAllowlist {
    private static final Set<String> MAVEN_GOALS = Collections.unmodifiableSet(
            new HashSet<String>(Arrays.asList("validate", "compile", "test", "verify", "package")));
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

    /**
     * Validates the complete process invocation, including subcommands and
     * arguments, before ProcessBuilder receives it.
     */
    public void requireAllowed(List<String> command) {
        if (command == null || command.isEmpty()) {
            throw new ToolException("Command is required");
        }
        requireAllowed(command.get(0));
        if ("git".equals(command.get(0))) {
            requireAllowedGit(command);
        } else if ("mvn".equals(command.get(0)) || "mvn.cmd".equals(command.get(0))) {
            requireAllowedMaven(command);
        } else {
            throw new ToolException("No argument policy exists for command: " + command.get(0));
        }
    }

    private static void requireAllowedGit(List<String> command) {
        if (command.equals(Arrays.asList("git", "diff", "--"))
                || command.equals(Arrays.asList("git", "apply", "--whitespace=nowarn", "-"))) {
            return;
        }
        if (command.size() == 3 && "branch".equals(command.get(1))
                && command.get(2).matches("[A-Za-z0-9][A-Za-z0-9._/-]{0,199}")) {
            return;
        }
        throw new ToolException("Git invocation is not allowed: " + command);
    }

    private static void requireAllowedMaven(List<String> command) {
        int index = 1;
        if (index < command.size() && "-q".equals(command.get(index))) {
            index++;
        }
        if (index + 1 < command.size() && "-pl".equals(command.get(index))) {
            requireSafeToken(command.get(index + 1), "Maven module");
            index += 2;
        }
        if (index < command.size() && command.get(index).startsWith("-Dtest=")) {
            requireSafeToken(command.get(index).substring("-Dtest=".length()), "Maven test selector");
            index++;
        }
        if (index != command.size() - 1 || !MAVEN_GOALS.contains(command.get(index))) {
            throw new ToolException("Maven invocation is not allowed: " + command);
        }
    }

    private static void requireSafeToken(String value, String label) {
        if (value == null || !value.matches("[A-Za-z0-9_.:-]+")) {
            throw new ToolException("Invalid " + label + ": " + value);
        }
    }
}

