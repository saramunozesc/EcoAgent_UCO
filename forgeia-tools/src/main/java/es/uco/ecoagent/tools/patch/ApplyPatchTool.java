package es.uco.ecoagent.tools.patch;

import es.uco.ecoagent.tools.api.Tool;
import es.uco.ecoagent.tools.api.ToolExecutionContext;
import es.uco.ecoagent.tools.command.CommandResult;
import es.uco.ecoagent.tools.command.CommandRunner;
import es.uco.ecoagent.tools.security.CommandAllowlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Applies unified diffs through git apply inside the workspace.
 */
public final class ApplyPatchTool implements Tool<ApplyPatchInput, ApplyPatchResult> {
    private final CommandRunner commandRunner;

    /**
     * Creates the tool with default command restrictions.
     */
    public ApplyPatchTool() {
        this(new CommandRunner(CommandAllowlist.defaults(), 10000L, 20000));
    }

    /**
     * Creates the tool with an injected command runner for tests.
     */
    public ApplyPatchTool(CommandRunner commandRunner) {
        this.commandRunner = commandRunner;
    }

    /**
     * Returns the stable tool name.
     */
    public String name() {
        return "applyPatch";
    }

    /**
     * Applies the diff through stdin and reports files mentioned by the patch.
     */
    public ApplyPatchResult execute(ToolExecutionContext context, ApplyPatchInput input) {
        CommandResult result = commandRunner.runWithInput(
                context.getWorkspace(),
                Arrays.asList("git", "apply", "--whitespace=nowarn", "-"),
                input.getUnifiedDiff());
        return new ApplyPatchResult(result.getExitCode(), parseModifiedFiles(input.getUnifiedDiff()), result.getOutput());
    }

    /**
     * Extracts affected paths from unified diff headers.
     */
    private static List<String> parseModifiedFiles(String unifiedDiff) {
        List<String> files = new ArrayList<String>();
        String[] lines = unifiedDiff.split("\\r?\\n");
        for (String line : lines) {
            if (line.startsWith("+++ b/")) {
                addUnique(files, line.substring("+++ b/".length()));
            } else if (line.startsWith("--- a/")) {
                addUnique(files, line.substring("--- a/".length()));
            }
        }
        return files;
    }

    /**
     * Adds a path once while ignoring diff null-device entries.
     */
    private static void addUnique(List<String> files, String path) {
        if (!"/dev/null".equals(path) && !files.contains(path)) {
            files.add(path);
        }
    }
}

