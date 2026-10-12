package es.uco.ecoagent.core.workspace;

import es.uco.ecoagent.core.config.ForgeiaConfig;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ProjectInitializerTest {
    @Test
    public void initCreatesEmptyWorkspaceAndRunLogWithoutIa() throws Exception {
        Path tempRoot = Files.createTempDirectory("forgeia-test");
        Path configFile = tempRoot.resolve("forgeia.properties");
        Path workspaceRoot = tempRoot.resolve("workspace");
        Path logsRoot = tempRoot.resolve("logs");
        Files.write(configFile, (
                "llm.provider=none\n"
                        + "llm.model=none\n"
                        + "workspace.root=" + workspaceRoot.toString().replace("\\", "\\\\") + "\n"
                        + "logs.root=" + logsRoot.toString().replace("\\", "\\\\") + "\n").getBytes("UTF-8"));

        ProjectInitializer initializer = new ProjectInitializer(ForgeiaConfig.load(configFile));
        InitResult result = initializer.init("Demo Project");

        assertEquals("Demo Project", result.getProject().getName());
        assertTrue(Files.isDirectory(result.getProject().getWorkspace()));
        assertEquals(0, Files.list(result.getProject().getWorkspace()).count());
        assertTrue(Files.isRegularFile(result.getLogFile()));
        String log = new String(Files.readAllBytes(result.getLogFile()), "UTF-8");
        assertTrue(log.contains("\"event\":\"PROJECT_WORKSPACE_CREATED\""));
        assertTrue(log.contains("\"llmProvider\":\"none\""));
    }
}

