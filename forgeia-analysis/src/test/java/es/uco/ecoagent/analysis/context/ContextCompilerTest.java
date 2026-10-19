package es.uco.ecoagent.analysis.context;

import es.uco.ecoagent.core.domain.TaskBudget;
import es.uco.ecoagent.core.domain.TaskStatus;
import es.uco.ecoagent.core.planning.PlannedTask;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
     * Includes a known target first, never duplicates paths and remains
     * deterministic across repeated compilations.
     */
    @Test
    public void targetSelectionIsUniqueAndDeterministic() throws Exception {
        Path root = Files.createTempDirectory("context-deterministic");
        write(root.resolve("TaskService.java"), "public class TaskService { public void createTask() {} }\n");
        write(root.resolve("TaskController.java"), "public class TaskController { public void listTasks() {} }\n");
        PlannedTask task = task(200, "TaskService.java");

        ContextSlice first = new ContextCompiler().compile(root, task);
        ContextSlice second = new ContextCompiler().compile(root, task);

        assertFalse(first.getItems().isEmpty());
        assertEquals("TaskService.java", first.getItems().get(0).getPath());
        Set<String> paths = new HashSet<String>();
        for (ContextItem item : first.getItems()) {
            assertTrue("Duplicate context path: " + item.getPath(), paths.add(item.getPath()));
        }
        assertEquivalent(first.getItems(), second.getItems());
        assertEquals(first.getTokenCount(), second.getTokenCount());
    }

    /**
     * Demonstrates the complete full file to snippet to summary to metadata
     * degradation chain as the available budget shrinks.
     */
    @Test
    public void degradesThroughEveryContextLevel() throws Exception {
        Path root = Files.createTempDirectory("context-levels");
        StringBuilder source = new StringBuilder("public class TaskService {\n");
        for (int index = 0; index < 80; index++) {
            source.append("  private String documentedField").append(index).append(" = \"fixture-value-")
                    .append(index).append("\";\n");
        }
        source.append("  public void createTask() {}\n");
        source.append("  public void completeTask() {}\n");
        source.append("}\n");
        write(root.resolve("TaskService.java"), source.toString());

        assertEquals(ContextLevel.L3_FULL_FILE, compileLevel(root, 2000));
        assertEquals(ContextLevel.L2_SNIPPET, compileLevel(root, 200));
        assertEquals(ContextLevel.L1_SUMMARY, compileLevel(root, 150));
        assertEquals(ContextLevel.L0_METADATA, compileLevel(root, 10));
    }

    private static ContextLevel compileLevel(Path root, int budget) {
        ContextSlice slice = new ContextCompiler().compile(root, task(budget, "TaskService.java"));
        assertTrue(slice.getTokenCount() <= budget);
        assertFalse("Expected one selected context item", slice.getItems().isEmpty());
        return slice.getItems().get(0).getLevel();
    }

    private static PlannedTask task(int budget, String targetFile) {
        return new PlannedTask(
                "TASK-020-1", "REQ-020", "Modificar TaskService", "SERVICE", TaskStatus.READY,
                Collections.singletonList(targetFile), Collections.<String>emptyList(),
                new TaskBudget(budget, 2500, 4, 2));
    }

    private static void assertEquivalent(List<ContextItem> first, List<ContextItem> second) {
        assertEquals(first.size(), second.size());
        for (int index = 0; index < first.size(); index++) {
            assertEquals(first.get(index).getPath(), second.get(index).getPath());
            assertEquals(first.get(index).getLevel(), second.get(index).getLevel());
            assertEquals(first.get(index).getContent(), second.get(index).getContent());
            assertEquals(first.get(index).getTokenEstimate(), second.get(index).getTokenEstimate());
            assertEquals(first.get(index).getScore(), second.get(index).getScore(), 0.0d);
        }
    }

    /**
     * Writes a UTF-8 fixture file.
     */
    private static void write(Path file, String text) throws Exception {
        Files.createDirectories(file.getParent());
        Files.write(file, text.getBytes("UTF-8"));
    }
}

