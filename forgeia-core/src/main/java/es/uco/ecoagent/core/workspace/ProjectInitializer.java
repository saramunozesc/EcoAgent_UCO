package es.uco.ecoagent.core.workspace;

import es.uco.ecoagent.core.config.ForgeiaConfig;
import es.uco.ecoagent.core.domain.Project;
import es.uco.ecoagent.core.domain.ProjectStatus;
import es.uco.ecoagent.core.logging.RunId;
import es.uco.ecoagent.core.logging.StructuredRunLogger;

import java.nio.file.Path;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Coordinates project initialization, sandbox creation and first run logging.
 */
public final class ProjectInitializer {
    private final ForgeiaConfig config;
    private final WorkspaceService workspaceService;

    /**
     * Creates an initializer from the external harness configuration.
     */
    public ProjectInitializer(ForgeiaConfig config) {
        this.config = config;
        this.workspaceService = new WorkspaceService(config.getWorkspaceRoot());
    }

    /**
     * Initializes a project workspace and records the operation as a run event.
     */
    public InitResult init(String projectName) {
        String runId = RunId.create();
        Path workspace = workspaceService.createProjectWorkspace(projectName);
        Project project = new Project(
                UUID.randomUUID().toString(),
                projectName,
                workspace,
                ProjectStatus.CREATED,
                Instant.now());

        StructuredRunLogger logger = new StructuredRunLogger(config.getLogsRoot(), runId);
        Map<String, String> fields = new LinkedHashMap<String, String>();
        fields.put("projectId", project.getId());
        fields.put("projectName", project.getName());
        fields.put("workspace", workspace.toAbsolutePath().normalize().toString());
        fields.put("llmProvider", config.getProvider());
        fields.put("llmModel", config.getModel());
        logger.event(runId, "PROJECT_WORKSPACE_CREATED", fields);

        return new InitResult(project, runId, logger.getLogFile());
    }
}

