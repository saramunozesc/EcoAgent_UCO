package es.uco.ecoagent.core.cli;

import es.uco.ecoagent.core.config.ForgeiaConfig;
import es.uco.ecoagent.core.workspace.InitResult;
import es.uco.ecoagent.core.workspace.ProjectInitializer;

public final class ForgeiaCli {
    private ForgeiaCli() {
    }

    public static void main(String[] args) {
        int exitCode = run(args);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

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

