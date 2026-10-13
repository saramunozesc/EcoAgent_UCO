package es.uco.ecoagent.analysis.project;

import es.uco.ecoagent.tools.command.CommandResult;
import es.uco.ecoagent.tools.command.CommandRunner;
import es.uco.ecoagent.tools.security.CommandAllowlist;

import java.nio.file.Path;
import java.util.List;

/**
 * Executes import-time git and Maven commands through the shared allowlist.
 */
public final class DefaultProjectCommandExecutor implements ProjectCommandExecutor {
    private final CommandRunner commandRunner;

    /**
     * Creates an executor with conservative timeout and output limits.
     */
    public DefaultProjectCommandExecutor() {
        this(new CommandRunner(CommandAllowlist.defaults(), 120000L, 20000));
    }

    /**
     * Creates an executor with an injected runner for integration tests.
     */
    public DefaultProjectCommandExecutor(CommandRunner commandRunner) {
        this.commandRunner = commandRunner;
    }

    /**
     * Runs a command without invoking a shell.
     */
    public CommandResult run(Path projectRoot, List<String> command) {
        return commandRunner.run(projectRoot, command);
    }
}
