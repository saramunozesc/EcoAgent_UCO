package es.uco.ecoagent.tools.maven;

import es.uco.ecoagent.tools.api.Tool;
import es.uco.ecoagent.tools.api.ToolExecutionContext;
import es.uco.ecoagent.tools.command.CommandResult;
import es.uco.ecoagent.tools.command.CommandRunner;
import es.uco.ecoagent.tools.security.CommandAllowlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Runs Maven tests with optional module and test selector.
 */
public final class TestTool implements Tool<TestInput, CommandResult> {
    private final CommandRunner commandRunner;

    /**
     * Creates the tool with default command restrictions.
     */
    public TestTool() {
        this(new CommandRunner(CommandAllowlist.defaults(), 120000L, 20000));
    }

    /**
     * Creates the tool with an injected command runner for tests.
     */
    public TestTool(CommandRunner commandRunner) {
        this.commandRunner = commandRunner;
    }

    /**
     * Returns the stable tool name.
     */
    public String name() {
        return "runTests";
    }

    /**
     * Executes Maven test with bounded output.
     */
    public CommandResult execute(ToolExecutionContext context, TestInput input) {
        List<String> command = new ArrayList<String>(Arrays.asList("mvn", "-q"));
        if (input.getModule() != null && !input.getModule().trim().isEmpty()) {
            MavenTool.validateToken(input.getModule(), "module");
            command.add("-pl");
            command.add(input.getModule());
        }
        if (input.getSelector() != null && !input.getSelector().trim().isEmpty()) {
            MavenTool.validateToken(input.getSelector(), "test selector");
            command.add("-Dtest=" + input.getSelector());
        }
        command.add("test");
        return commandRunner.run(context.getWorkspace(), command);
    }
}

