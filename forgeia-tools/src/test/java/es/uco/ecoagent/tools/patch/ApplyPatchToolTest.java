package es.uco.ecoagent.tools.patch;

import org.junit.Test;

import java.lang.reflect.Method;
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
}

