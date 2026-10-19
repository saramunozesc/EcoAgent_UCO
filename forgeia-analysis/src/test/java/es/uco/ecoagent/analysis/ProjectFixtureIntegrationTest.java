package es.uco.ecoagent.analysis;

import es.uco.ecoagent.analysis.javaast.JavaAstIndexer;
import es.uco.ecoagent.analysis.javaast.JavaFileSummary;
import es.uco.ecoagent.tools.api.ToolExecutionContext;
import es.uco.ecoagent.tools.command.CommandResult;
import es.uco.ecoagent.tools.maven.MavenInput;
import es.uco.ecoagent.tools.maven.MavenTool;
import org.junit.Test;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Exercises Maven and source indexing against a versioned CRUD fixture.
 */
public final class ProjectFixtureIntegrationTest {
    @Test
    public void fixtureCoversCrudValidationRelationAndEndpointAndBuilds() throws Exception {
        Path fixture = fixture("/fixtures/task-crud");
        List<JavaFileSummary> summaries = new JavaAstIndexer().index(fixture);

        JavaFileSummary task = find(summaries, "Task");
        JavaFileSummary service = find(summaries, "TaskService");
        JavaFileSummary controller = find(summaries, "TaskController");
        assertTrue(task.getAnnotations().contains("Entity"));
        assertTrue(task.getAnnotations().contains("ManyToOne"));
        assertTrue(service.getMethods().size() >= 3);
        assertTrue(controller.getAnnotations().contains("RestController"));
        assertTrue(controller.getAnnotations().contains("GetMapping"));
        assertTrue(controller.getAnnotations().contains("PostMapping"));

        Path copy = Files.createTempDirectory("forgeia-crud-fixture");
        copyTree(fixture, copy);
        CommandResult result = new MavenTool().execute(new ToolExecutionContext(copy), new MavenInput("test", null));

        assertEquals(result.getOutput(), 0, result.getExitCode());
        assertEquals(false, result.isTimedOut());
    }

    private static JavaFileSummary find(List<JavaFileSummary> summaries, String type) {
        for (JavaFileSummary summary : summaries) {
            if (type.equals(summary.getPrimaryType())) {
                return summary;
            }
        }
        throw new AssertionError("Missing fixture type: " + type);
    }

    private static Path fixture(String name) throws Exception {
        URL resource = ProjectFixtureIntegrationTest.class.getResource(name);
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
