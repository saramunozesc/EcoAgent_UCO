package es.uco.ecoagent.tools.command;

import es.uco.ecoagent.tools.api.ToolException;
import es.uco.ecoagent.tools.security.CommandAllowlist;
import es.uco.ecoagent.tools.security.CommandEnvironmentSanitizer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Runs allowlisted processes with a timeout and bounded output.
 */
public final class CommandRunner {
    private final CommandAllowlist allowlist;
    private final long timeoutMillis;
    private final int maxOutputChars;

    /**
     * Creates a runner with command restrictions and output limits.
     */
    public CommandRunner(CommandAllowlist allowlist, long timeoutMillis, int maxOutputChars) {
        this.allowlist = allowlist;
        this.timeoutMillis = timeoutMillis;
        this.maxOutputChars = maxOutputChars;
    }

    /**
     * Executes a command in a working directory without using a shell.
     */
    public CommandResult run(Path workingDirectory, List<String> command) {
        return runWithInput(workingDirectory, command, null);
    }

    /**
     * Executes a command and optionally writes text to stdin.
     */
    public CommandResult runWithInput(Path workingDirectory, List<String> command, String input) {
        if (command == null || command.isEmpty()) {
            throw new ToolException("Command is required");
        }
        allowlist.requireAllowed(command);

        ProcessBuilder processBuilder = new ProcessBuilder(platformCommand(command));
        processBuilder.directory(workingDirectory.toFile());
        processBuilder.redirectErrorStream(true);
        CommandEnvironmentSanitizer.removeSensitiveVariables(processBuilder.environment());
        try {
            Process process = processBuilder.start();
            if (input != null) {
                try (OutputStream stdin = process.getOutputStream()) {
                    stdin.write(input.getBytes(StandardCharsets.UTF_8));
                }
            } else {
                process.getOutputStream().close();
            }

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            StreamCollector collector = new StreamCollector(process.getInputStream(), output);
            Thread thread = new Thread(collector, "forgeia-command-output");
            thread.start();

            boolean completed = process.waitFor(timeoutMillis, TimeUnit.MILLISECONDS);
            if (!completed) {
                process.destroyForcibly();
                thread.join(1000L);
                return new CommandResult(-1, summarize(output.toString("UTF-8")), true);
            }

            thread.join(1000L);
            return new CommandResult(process.exitValue(), summarize(output.toString("UTF-8")), false);
        } catch (IOException exception) {
            throw new ToolException("Cannot execute command: " + command, exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ToolException("Command interrupted: " + command, exception);
        }
    }

    /**
     * Uses the Windows command wrapper Maven installs while keeping the
     * logical allowlisted invocation platform independent.
     */
    private static List<String> platformCommand(List<String> command) {
        List<String> resolved = new ArrayList<String>(command);
        String operatingSystem = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (operatingSystem.contains("win") && "mvn".equals(resolved.get(0))) {
            resolved.set(0, "mvn.cmd");
        }
        return resolved;
    }

    /**
     * Truncates command output to keep tool results compact.
     */
    private String summarize(String output) {
        if (output.length() <= maxOutputChars) {
            return output;
        }
        return output.substring(0, maxOutputChars) + "\n...[truncated]";
    }
}

