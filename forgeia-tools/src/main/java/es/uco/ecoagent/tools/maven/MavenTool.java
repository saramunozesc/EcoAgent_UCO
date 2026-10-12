package es.uco.ecoagent.tools.maven;

import es.uco.ecoagent.tools.api.Tool;
import es.uco.ecoagent.tools.api.ToolException;
import es.uco.ecoagent.tools.api.ToolExecutionContext;
import es.uco.ecoagent.tools.command.CommandResult;
import es.uco.ecoagent.tools.command.CommandRunner;
import es.uco.ecoagent.tools.security.CommandAllowlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Runs Maven goals through an allowlisted, timeout-bound process.
 */
public final class MavenTool implements Tool<MavenInput, CommandResult> {
    private final CommandRunner commandRunner;

    /**
     * Creates the tool with default command restrictions.
     */
    public MavenTool() {
        this(new CommandRunner(CommandAllowlist.defaults(), 120000L, 20000));
    }

    /**
     * Creates the tool with an injected command runner for tests.
     */
    public MavenTool(CommandRunner commandRunner) {
        this.commandRunner = commandRunner;
    }

    /**
     * Returns the stable tool name.
     */
    public String name() {
        return "runMaven";
    }

    /**
     * Executes a safe Maven goal and returns a compact result.
     */
    public CommandResult execute(ToolExecutionContext context, MavenInput input) {
        validateToken(input.getGoal(), "goal");
        List<String> command = new ArrayList<String>(Arrays.asList("mvn", "-q"));
        if (input.getModule() != null && !input.getModule().trim().isEmpty()) {
            validateToken(input.getModule(), "module");
            command.add("-pl");
            command.add(input.getModule());
        }
        command.add(input.getGoal());
        return commandRunner.run(context.getWorkspace(), command);
    }

    /**
     * Rejects values that could become shell-like arguments.
     */
    static void validateToken(String value, String label) {
        if (value == null || !value.matches("[A-Za-z0-9_.:-]+")) {
            throw new ToolException("Invalid Maven " + label + ": " + value);
        }
    }
}

