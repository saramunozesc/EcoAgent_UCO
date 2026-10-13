package es.uco.ecoagent.llm.requirements;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Verifies phase 2 requirement generation without a real LLM provider.
 */
public class RequirementsAgentTest {
    /**
     * Ensures three ideas produce valid, persisted and traceable requirements.
     */
    @Test
    public void generatesRequirementsForThreeIdeas() throws Exception {
        FakeLlmClient client = new FakeLlmClient()
                .add(spec("MathKids", "CHILD", "Practicar tablas", "Resolver ejercicios", "MUST"), 100, 80)
                .add(spec("TaskBoard", "USER", "Crear tareas", "Se puede crear una tarea", "SHOULD"), 90, 70)
                .add(spec("Library", "LIBRARIAN", "Gestionar libros", "Se puede registrar un libro", "COULD"), 95, 75);
        RequirementsAgent agent = new RequirementsAgent(client, new RequirementsTraceStore());
        Path root = Files.createTempDirectory("requirements-agent");

        RequirementGenerationResult first = agent.generate("App infantil de matematicas", root.resolve("idea-1"));
        RequirementGenerationResult second = agent.generate("Tablero de tareas", root.resolve("idea-2"));
        RequirementGenerationResult third = agent.generate("Sistema de biblioteca", root.resolve("idea-3"));

        assertEquals("REQ-001", first.getSpec().getRequirements().get(0).getId());
        assertEquals("REQ-001", second.getSpec().getRequirements().get(0).getId());
        assertEquals("REQ-001", third.getSpec().getRequirements().get(0).getId());
        assertTrue(Files.isRegularFile(first.getSpecFile()));
        assertTrue(Files.isRegularFile(second.getSpecFile()));
        assertTrue(Files.isRegularFile(third.getSpecFile()));
        assertTrue(new String(Files.readAllBytes(first.getTraceFile()), "UTF-8").contains("\"inputTokens\":100"));
        assertEquals(3, client.getCalls());
    }

    /**
     * Ensures invalid JSON triggers one repair request before validation.
     */
    @Test
    public void repairsInvalidJsonOnce() throws Exception {
        FakeLlmClient client = new FakeLlmClient()
                .add("not json", 10, 3)
                .add(spec("RepairDemo", "USER", "Registro", "Se valida JSON", "MUST"), 20, 12);
        RequirementsAgent agent = new RequirementsAgent(client, new RequirementsTraceStore());

        RequirementGenerationResult result = agent.generate("Idea con respuesta rota", Files.createTempDirectory("requirements-repair"));

        assertEquals("RepairDemo", result.getSpec().getProjectName());
        assertEquals(2, client.getCalls());
    }

    /**
     * Builds a compact valid requirements JSON fixture.
     */
    private static String spec(String projectName, String actor, String title, String criterion, String priority) {
        return "{"
                + "\"projectName\":\"" + projectName + "\","
                + "\"actors\":[\"" + actor + "\"],"
                + "\"requirements\":[{"
                + "\"id\":\"IGNORED-BY-AGENT\","
                + "\"title\":\"" + title + "\","
                + "\"acceptanceCriteria\":[\"" + criterion + "\"],"
                + "\"priority\":\"" + priority + "\""
                + "}]"
                + "}";
    }
}

