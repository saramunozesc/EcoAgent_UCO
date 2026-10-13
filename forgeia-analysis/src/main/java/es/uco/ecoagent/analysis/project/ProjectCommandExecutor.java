package es.uco.ecoagent.analysis.project;

import es.uco.ecoagent.tools.command.CommandResult;

import java.nio.file.Path;
import java.util.List;

/**
 * Executes deterministic project commands during import.
 */
public interface ProjectCommandExecutor {
    /**
     * Runs a command in the imported project root.
     */
    CommandResult run(Path projectRoot, List<String> command);
}
