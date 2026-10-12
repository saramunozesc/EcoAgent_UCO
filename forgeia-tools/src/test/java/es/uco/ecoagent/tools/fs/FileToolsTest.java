package es.uco.ecoagent.tools.fs;

import es.uco.ecoagent.tools.api.ToolExecutionContext;
import es.uco.ecoagent.tools.search.SearchTextInput;
import es.uco.ecoagent.tools.search.SearchTextResult;
import es.uco.ecoagent.tools.search.SearchTextTool;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Verifies deterministic filesystem tools inside a workspace.
 */
public class FileToolsTest {
    /**
     * Ensures write, read, list and search return structured results.
     */
    @Test
    public void fileToolsOperateInsideWorkspace() throws Exception {
        Path workspace = Files.createTempDirectory("forgeia-tools");
        ToolExecutionContext context = new ToolExecutionContext(workspace);

        WriteFileResult writeResult = new WriteFileTool().execute(context, new WriteFileInput("src/demo.txt", "one\ntwo\nthree"));
        ReadFileResult readResult = new ReadFileTool().execute(context, new ReadFileInput("src/demo.txt", 2, 3));
        ListFilesResult listResult = new ListFilesTool().execute(context, new ListFilesInput(".", ".txt"));
        SearchTextResult searchResult = new SearchTextTool().execute(context, new SearchTextInput("two"));

        assertEquals("src/demo.txt", writeResult.getPath());
        assertEquals(64, writeResult.getSha256().length());
        assertEquals("two" + System.lineSeparator() + "three", readResult.getText());
        assertEquals("src/demo.txt", listResult.getFiles().get(0));
        assertEquals(1, searchResult.getMatches().size());
        assertEquals(2, searchResult.getMatches().get(0).getLineNumber());
    }

    /**
     * Ensures the path policy blocks traversal outside the workspace.
     */
    @Test
    public void writeRejectsPathTraversal() throws Exception {
        Path workspace = Files.createTempDirectory("forgeia-tools");
        ToolExecutionContext context = new ToolExecutionContext(workspace);

        boolean rejected = false;
        try {
            new WriteFileTool().execute(context, new WriteFileInput("../outside.txt", "bad"));
        } catch (RuntimeException expected) {
            rejected = true;
        }

        assertTrue(rejected);
    }
}

