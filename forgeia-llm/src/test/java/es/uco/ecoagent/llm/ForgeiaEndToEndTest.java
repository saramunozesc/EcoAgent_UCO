package es.uco.ecoagent.llm;

import es.uco.ecoagent.core.domain.Requirement;
import es.uco.ecoagent.core.domain.RequirementStatus;
import es.uco.ecoagent.core.domain.TaskBudget;
import es.uco.ecoagent.core.planning.PlannedTask;
import es.uco.ecoagent.core.planning.RequirementPlanner;
import es.uco.ecoagent.llm.code.CodeAgent;
import es.uco.ecoagent.llm.code.CodeTaskInput;
import es.uco.ecoagent.llm.code.PatchProposal;
import es.uco.ecoagent.llm.requirements.FakeLlmClient;
import es.uco.ecoagent.llm.requirements.RequirementGenerationResult;
import es.uco.ecoagent.llm.requirements.RequirementItem;
import es.uco.ecoagent.llm.requirements.RequirementsAgent;
import es.uco.ecoagent.llm.requirements.RequirementsTraceStore;
import es.uco.ecoagent.tools.api.ToolExecutionContext;
import es.uco.ecoagent.tools.command.CommandResult;
import es.uco.ecoagent.tools.maven.MavenInput;
import es.uco.ecoagent.tools.maven.MavenTool;
import es.uco.ecoagent.tools.patch.ApplyPatchInput;
import es.uco.ecoagent.tools.patch.ApplyPatchResult;
import es.uco.ecoagent.tools.patch.ApplyPatchTool;
import es.uco.ecoagent.tools.verification.ErrorNormalizer;
import org.junit.Test;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Covers idea -> requirement -> plan -> code patch -> build -> tests.
 */
public final class ForgeiaEndToEndTest {
    @Test
    public void repairsFixtureFromNaturalLanguageIdeaToPassingBuild() throws Exception {
        Path workspace = Files.createTempDirectory("forgeia-e2e");
        copyTree(fixture("/fixtures/priority-e2e"), workspace);
        Process gitInit = new ProcessBuilder("git", "init").directory(workspace.toFile()).start();
        assertEquals(0, gitInit.waitFor());

        ToolExecutionContext context = new ToolExecutionContext(workspace);
        CommandResult failingBuild = new MavenTool().execute(context, new MavenInput("test", null));
        assertTrue("Fixture must start broken", failingBuild.getExitCode() != 0);
        assertFalse(new ErrorNormalizer().normalize(failingBuild.getOutput()).isEmpty());

        FakeLlmClient requirementsClient = new FakeLlmClient().add("{"
                + "\"projectName\":\"TaskApp\",\"actors\":[\"USER\"],"
                + "\"requirements\":[{\"title\":\"Añadir prioridad\","
                + "\"acceptanceCriteria\":[\"Admite LOW MEDIUM HIGH\"],\"priority\":\"MUST\"}]}",
                40, 30);
        RequirementGenerationResult generated = new RequirementsAgent(requirementsClient, new RequirementsTraceStore())
                .generate("Añade prioridad HIGH MEDIUM LOW a las tareas", workspace.resolve(".forgeia"));
        RequirementItem item = generated.getSpec().getRequirements().get(0);
        Requirement requirement = new Requirement(item.getId(), item.getTitle(), item.getTitle(),
                item.getAcceptanceCriteria(), item.getPriority(), RequirementStatus.APPROVED);
        PlannedTask task = new RequirementPlanner(new TaskBudget(8000, 2500, 4, 2))
                .plan(requirement).getTasks().get(0);

        String diff = "diff --git a/src/main/java/demo/Priority.java b/src/main/java/demo/Priority.java\n"
                + "new file mode 100644\n--- /dev/null\n+++ b/src/main/java/demo/Priority.java\n"
                + "@@ -0,0 +1,7 @@\n+package demo;\n+\n+public enum Priority {\n"
                + "+    LOW,\n+    MEDIUM,\n+    HIGH\n+}\n";
        FakeLlmClient codeClient = new FakeLlmClient().add("{"
                + "\"taskId\":\"" + task.getId() + "\",\"summary\":\"Crea Priority\","
                + "\"unifiedDiff\":\"" + json(diff) + "\","
                + "\"files\":[\"src/main/java/demo/Priority.java\"]}", 80, 40);
        PatchProposal proposal = new CodeAgent(codeClient).propose(
                new CodeTaskInput(task, "Java 8", "Task depende del enum Priority"));
        ApplyPatchResult applied = new ApplyPatchTool().execute(context,
                new ApplyPatchInput(proposal.getUnifiedDiff()));

        assertEquals(applied.getOutput(), 0, applied.getExitCode());
        assertTrue(Files.exists(workspace.resolve("src/main/java/demo/Priority.java")));
        CommandResult passingBuild = new MavenTool().execute(context, new MavenInput("test", null));
        assertEquals(passingBuild.getOutput(), 0, passingBuild.getExitCode());
        assertTrue(Files.exists(generated.getSpecFile()));
        assertTrue(Files.exists(generated.getTraceFile()));
    }

    private static String json(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }

    private static Path fixture(String name) throws Exception {
        URL resource = ForgeiaEndToEndTest.class.getResource(name);
        if (resource == null) {
            throw new AssertionError("Missing fixture: " + name);
        }
        return Paths.get(resource.toURI());
    }

    private static void copyTree(Path source, Path target) throws Exception {
        Files.walk(source).forEach(path -> {
            try {
                Path destination = target.resolve(source.relativize(path).toString());
                if (Files.isDirectory(path)) {
                    Files.createDirectories(destination);
                } else {
                    Files.copy(path, destination, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (Exception exception) {
                throw new IllegalStateException(exception);
            }
        });
    }
}
