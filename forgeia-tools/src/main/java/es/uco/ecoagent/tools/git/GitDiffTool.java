package es.uco.ecoagent.tools.git;

import es.uco.ecoagent.tools.api.Tool;
import es.uco.ecoagent.tools.api.ToolExecutionContext;
import es.uco.ecoagent.tools.command.CommandResult;
import es.uco.ecoagent.tools.command.CommandRunner;
import es.uco.ecoagent.tools.security.CommandAllowlist;

import java.util.Arrays;

/**
 * Produces a bounded Git diff for the authorized workspace.
 */
public final class GitDiffTool implements Tool<GitDiffInput, GitDiffResult> {
    private final CommandRunner commandRunner;

    /**
     * Creates the tool with the default allowlist and timeout.
     */
    public GitDiffTool() {
        this(new CommandRunner(CommandAllowlist.defaults(), 10000L, 20000));
    }

    /**
     * Creates the tool with an injected command runner for tests.
     */
    public GitDiffTool(CommandRunner commandRunner) {
        this.commandRunner = commandRunner;
    }

    /**
     * Returns the stable tool name.
     */
    public String name() {
        return "gitDiff";
    }

    /**
     * Runs git diff without invoking a shell.
     */
    public GitDiffResult execute(ToolExecutionContext context, GitDiffInput input) {
        CommandResult result = commandRunner.run(context.getWorkspace(), Arrays.asList("git", "diff", "--"));
        return new GitDiffResult(result.getOutput());
    }
}

