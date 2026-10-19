package es.uco.ecoagent.orchestrator.run;

import es.uco.ecoagent.core.domain.TaskBudget;
import es.uco.ecoagent.core.domain.TaskStatus;
import es.uco.ecoagent.core.orchestration.OrchestrationState;
import es.uco.ecoagent.llm.api.LlmClient;
import es.uco.ecoagent.llm.api.LlmRequest;
import es.uco.ecoagent.llm.api.LlmResponse;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Queue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Exercises the production orchestration service with a deterministic model.
 */
public final class ForgeiaRunServiceTest {
    @Test
    public void executesRequirementContextPatchBuildAndMetrics() throws Exception {
        Path workspace = Files.createTempDirectory("forgeia-run-service");
        writeFixture(workspace);
        assertEquals(0, new ProcessBuilder("git", "init").directory(workspace.toFile()).start().waitFor());

        QueueLlmClient llm = new QueueLlmClient();
        llm.add("{\"projectName\":\"TaskApp\",\"actors\":[\"USER\"],\"requirements\":[{"
                + "\"title\":\"Agregar prioridad\",\"acceptanceCriteria\":[\"Existe Priority\"],"
                + "\"priority\":\"MUST\"}]}", 30, 20);
        String diff = "diff --git a/src/main/java/demo/Priority.java b/src/main/java/demo/Priority.java\n"
                + "new file mode 100644\n--- /dev/null\n+++ b/src/main/java/demo/Priority.java\n"
                + "@@ -0,0 +1,7 @@\n+package demo;\n+\n+public enum Priority {\n"
                + "+    LOW,\n+    MEDIUM,\n+    HIGH\n+}\n";
        llm.add("{\"taskId\":\"TASK-001-1\",\"summary\":\"Crea Priority\","
                + "\"unifiedDiff\":\"" + json(diff) + "\","
                + "\"files\":[\"src/main/java/demo/Priority.java\"]}", 70, 35);

        ForgeiaRunResult result = new ForgeiaRunService(llm, new TaskBudget(8000, 2500, 4, 2))
                .execute(new ForgeiaRunRequest(workspace,
                        "Agrega prioridad HIGH MEDIUM LOW a las tareas", "Java 8 con Maven"));

        assertEquals(TaskStatus.DONE, result.getStatus());
        assertEquals(OrchestrationState.DONE, result.getOrchestrationState());
        assertEquals(2, result.getUsage().getCalls());
        assertEquals(100, result.getUsage().getInputTokens());
        assertEquals(55, result.getUsage().getOutputTokens());
        assertEquals(0, result.getRepairAttempts());
        assertTrue(result.getContextSlice().getTokenCount() > 0);
        assertTrue(Files.isRegularFile(workspace.resolve("src/main/java/demo/Priority.java")));
        assertTrue(Files.isRegularFile(result.getArtifactPath()));
        assertTrue(Files.isRegularFile(result.getArtifactPath().getParent().resolve("context.txt")));
        assertTrue(Files.isRegularFile(result.getArtifactPath().getParent().resolve("final.patch")));
        assertTrue(Files.isRegularFile(workspace.resolve("blackboard/metrics.jsonl")));
        String resultJson = new String(Files.readAllBytes(result.getArtifactPath()), StandardCharsets.UTF_8);
        assertTrue(resultJson.contains("\"orchestrationState\": \"DONE\""));
        assertTrue(resultJson.contains("\"contextTokens\":"));
    }

    @Test
    public void repairsACompilationFailureWithinTheConfiguredBudget() throws Exception {
        Path workspace = Files.createTempDirectory("forgeia-run-repair");
        writeFixture(workspace);
        assertEquals(0, new ProcessBuilder("git", "init").directory(workspace.toFile()).start().waitFor());

        QueueLlmClient llm = new QueueLlmClient();
        llm.add("{\"projectName\":\"TaskApp\",\"actors\":[\"USER\"],\"requirements\":[{"
                + "\"title\":\"Agregar prioridad\",\"acceptanceCriteria\":[\"Existe Priority\"],"
                + "\"priority\":\"MUST\"}]}", 30, 20);
        String broken = "diff --git a/src/main/java/demo/Priority.java b/src/main/java/demo/Priority.java\n"
                + "new file mode 100644\n--- /dev/null\n+++ b/src/main/java/demo/Priority.java\n"
                + "@@ -0,0 +1,7 @@\n+package demo;\n+\n+public enum Priority {\n"
                + "+    LOW,\n+    MEDIUM,\n+    HIGH BROKEN\n+}\n";
        llm.add(proposal("TASK-001-1", "Cambio con fallo", broken), 60, 30);
        String repair = "diff --git a/src/main/java/demo/Priority.java b/src/main/java/demo/Priority.java\n"
                + "--- a/src/main/java/demo/Priority.java\n+++ b/src/main/java/demo/Priority.java\n"
                + "@@ -3,5 +3,5 @@\n public enum Priority {\n     LOW,\n     MEDIUM,\n"
                + "-    HIGH BROKEN\n+    HIGH\n }\n";
        llm.add(proposal("TASK-001-1", "Repara Priority", repair), 45, 25);

        ForgeiaRunResult result = new ForgeiaRunService(llm, new TaskBudget(8000, 2500, 4, 2))
                .execute(new ForgeiaRunRequest(workspace, "Agrega prioridad", "Java 8 con Maven"));

        assertEquals(TaskStatus.DONE, result.getStatus());
        assertEquals(1, result.getRepairAttempts());
        assertEquals(3, result.getUsage().getCalls());
        String priority = new String(Files.readAllBytes(
                workspace.resolve("src/main/java/demo/Priority.java")), StandardCharsets.UTF_8);
        assertTrue(priority.contains("HIGH"));
        assertTrue(!priority.contains("BROKEN"));
    }

    private static void writeFixture(Path root) throws Exception {
        write(root.resolve("pom.xml"), "<project xmlns=\"http://maven.apache.org/POM/4.0.0\">"
                + "<modelVersion>4.0.0</modelVersion><groupId>demo</groupId><artifactId>task-app</artifactId>"
                + "<version>1</version><properties><maven.compiler.source>1.8</maven.compiler.source>"
                + "<maven.compiler.target>1.8</maven.compiler.target></properties></project>");
        write(root.resolve("src/main/java/demo/Task.java"),
                "package demo; public final class Task { private String title; }\n");
    }

    private static void write(Path path, String content) throws Exception {
        Files.createDirectories(path.getParent());
        Files.write(path, content.getBytes(StandardCharsets.UTF_8));
    }

    private static String json(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }

    private static String proposal(String taskId, String summary, String diff) {
        return "{\"taskId\":\"" + taskId + "\",\"summary\":\"" + summary
                + "\",\"unifiedDiff\":\"" + json(diff)
                + "\",\"files\":[\"src/main/java/demo/Priority.java\"]}";
    }

    private static final class QueueLlmClient implements LlmClient {
        private final Queue<LlmResponse> responses = new ArrayDeque<LlmResponse>();

        private void add(String content, int inputTokens, int outputTokens) {
            responses.add(new LlmResponse("test", "deterministic", content,
                    inputTokens, outputTokens, 5L));
        }

        public LlmResponse complete(LlmRequest request) {
            if (responses.isEmpty()) {
                throw new AssertionError("Unexpected LLM call for " + request.getMetadata());
            }
            return responses.remove();
        }
    }
}
