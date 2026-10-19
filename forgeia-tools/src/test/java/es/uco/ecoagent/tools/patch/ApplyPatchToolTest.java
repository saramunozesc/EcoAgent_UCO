package es.uco.ecoagent.tools.patch;

import es.uco.ecoagent.tools.api.ToolException;
import es.uco.ecoagent.tools.api.ToolExecutionContext;
import org.junit.Test;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Verifies patch metadata extraction without applying a real diff.
 */
public class ApplyPatchToolTest {
    /**
     * Ensures modified files are extracted from unified diff headers.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void parsesModifiedFiles() throws Exception {
        Method method = ApplyPatchTool.class.getDeclaredMethod("parseModifiedFiles", String.class);
        method.setAccessible(true);

        List<String> files = (List<String>) method.invoke(null, "--- a/demo.txt\n+++ b/demo.txt\n@@\n-old\n+new\n");

        assertEquals(1, files.size());
        assertEquals("demo.txt", files.get(0));
    }

    /**
     * Rejects patch headers that attempt to leave the authorized workspace.
     */
    @Test(expected = ToolException.class)
    public void rejectsPatchPathTraversalBeforeRunningGit() throws Exception {
        Path workspace = Files.createTempDirectory("forgeia-patch-security");
        String diff = "--- /dev/null\n+++ b/../../outside.txt\n@@\n+bad\n";

        new ApplyPatchTool().execute(new ToolExecutionContext(workspace), new ApplyPatchInput(diff));
    }

    /**
     * File deletion is denied by the default execution context.
     */
    @Test(expected = ToolException.class)
    public void deletionRequiresExplicitApprovalBeforeRunningGit() throws Exception {
        Path workspace = Files.createTempDirectory("forgeia-patch-delete");
        Files.write(workspace.resolve("demo.txt"), "old".getBytes("UTF-8"));
        String diff = "--- a/demo.txt\n+++ /dev/null\n@@ -1 +0,0 @@\n-old\n";

        new ApplyPatchTool().execute(new ToolExecutionContext(workspace), new ApplyPatchInput(diff));
    }
}

