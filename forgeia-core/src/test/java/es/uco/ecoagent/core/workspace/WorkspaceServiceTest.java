package es.uco.ecoagent.core.workspace;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Verifies workspace creation stays inside the configured sandbox.
 */
public class WorkspaceServiceTest {
    /**
     * Ensures readable names are converted into safe workspace folders.
     */
    @Test
    public void createsWorkspaceInsideSandboxWithSafeName() throws Exception {
        Path root = Files.createTempDirectory("forgeia-workspace");
        WorkspaceService service = new WorkspaceService(root);

        Path workspace = service.createProjectWorkspace("Mi Proyecto 01");

        assertTrue(workspace.startsWith(root.toAbsolutePath().normalize()));
        assertEquals("mi-proyecto-01", workspace.getFileName().toString());
        assertTrue(Files.isDirectory(workspace));
    }

    /**
     * Ensures unsafe names are rejected before touching the filesystem.
     */
    @Test(expected = IllegalArgumentException.class)
    public void rejectsInvalidName() throws Exception {
        Path root = Files.createTempDirectory("forgeia-workspace");
        new WorkspaceService(root).createProjectWorkspace("...");
    }
}

