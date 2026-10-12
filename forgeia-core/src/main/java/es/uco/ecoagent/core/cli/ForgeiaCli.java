package es.uco.ecoagent.core.cli;

import es.uco.ecoagent.core.config.ForgeiaConfig;
import es.uco.ecoagent.core.workspace.InitResult;
import es.uco.ecoagent.core.workspace.ProjectInitializer;

/**
 * Entry point for the minimal ForgeIA command line interface.
 */
public final class ForgeiaCli {
    /**
     * Prevents instantiation because the CLI only exposes static behavior.
     */
    private ForgeiaCli() {
    }

    /**
     * Starts the CLI process and exits with a non-zero status on errors.
     */
    public static void main(String[] args) {
        int exitCode = run(args);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    /**
     * Parses supported commands and executes the requested action.
     */
    static int run(String[] args) {
        if (args.length != 2 || !"init".equals(args[0])) {
            System.err.println("Usage: forgeia init <nombre>");
            return 2;
        }

        ForgeiaConfig config = ForgeiaConfig.loadDefault();
        ProjectInitializer initializer = new ProjectInitializer(config);
        InitResult result = initializer.init(args[1]);

        System.out.println("Project initialized: " + result.getProject().getName());
        System.out.println("Workspace: " + result.getProject().getWorkspace().toAbsolutePath().normalize());
        System.out.println("RunId: " + result.getRunId());
        System.out.println("Log: " + result.getLogFile().toAbsolutePath().normalize());
        return 0;
    }
}

