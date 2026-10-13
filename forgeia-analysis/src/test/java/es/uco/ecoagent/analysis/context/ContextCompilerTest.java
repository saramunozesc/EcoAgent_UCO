package es.uco.ecoagent.analysis.context;

import es.uco.ecoagent.core.domain.TaskBudget;
import es.uco.ecoagent.core.domain.TaskStatus;
import es.uco.ecoagent.core.planning.PlannedTask;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Verifies context selection and budget enforcement.
 */
public class ContextCompilerTest {
    /**
     * Ensures relevant files are ranked into a context slice under budget.
     */
    @Test
    public void compilesContextSliceUnderBudget() throws Exception {
        Path root = Files.createTempDirectory("context-compiler");
        write(root.resolve("ScoreService.java"), "import demo.ScoreRepository;\npublic class ScoreService { public void saveScore() {} }\n");
        write(root.resolve("UnrelatedController.java"), "public class UnrelatedController { public void ping() {} }\n");
        PlannedTask task = new PlannedTask(
                "TASK-004-3",
                "REQ-004",
                "Implementar ScoreService",
                "SERVICE",
                TaskStatus.READY,
                Collections.singletonList("ScoreService.java"),
                Collections.<String>emptyList(),
                new TaskBudget(120, 2500, 4, 2));

        ContextSlice slice = new ContextCompiler().compile(root, task);

        assertEquals("TASK-004-3", slice.getTaskId());
        assertTrue(slice.getTokenCount() <= 120);
        assertFalse(slice.getItems().isEmpty());
        assertEquals("ScoreService.java", slice.getItems().get(0).getPath());
    }

    /**
     * Ensures tight budgets degrade from full file to smaller context levels.
     */
    @Test
    public void degradesContextWhenBudgetIsTight() throws Exception {
        Path root = Files.createTempDirectory("context-budget");
        write(root.resolve("ScoreService.java"), "import demo.ScoreRepository;\npublic class ScoreService { public void saveScore() {} public void anotherMethod() {} }\n");
        PlannedTask task = new PlannedTask(
                "TASK-004-3",
                "REQ-004",
                "Implementar ScoreService",
                "SERVICE",
                TaskStatus.READY,
                Collections.singletonList("ScoreService.java"),
                Collections.<String>emptyList(),
                new TaskBudget(12, 2500, 4, 2));

        ContextSlice slice = new ContextCompiler().compile(root, task);

        assertTrue(slice.getTokenCount() <= 12);
        assertTrue(slice.getItems().isEmpty() || slice.getItems().get(0).getLevel() != ContextLevel.L3_FULL_FILE);
    }

    /**
     * Writes a UTF-8 fixture file.
     */
    private static void write(Path file, String text) throws Exception {
        Files.createDirectories(file.getParent());
        Files.write(file, text.getBytes("UTF-8"));
    }
}

