package es.uco.ecoagent.analysis.project;

import es.uco.ecoagent.analysis.graph.DependencyGraphBuilder;
import es.uco.ecoagent.analysis.javaast.JavaAstIndexer;
import es.uco.ecoagent.tools.command.CommandResult;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Verifies deterministic import of an existing Maven project.
 */
public final class ExistingProjectImporterTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    /**
     * Imports a Maven project and persists its summary without real git or Maven calls.
     */
    @Test
    public void importsExistingMavenProject() throws Exception {
        Path root = temporaryFolder.newFolder("sample").toPath();
        Files.write(root.resolve("pom.xml"), "<project></project>".getBytes(StandardCharsets.UTF_8));
        Path source = root.resolve("src/main/java/demo/UserController.java");
        Files.createDirectories(source.getParent());
        Files.write(source, ("package demo;\n"
                + "import demo.User;\n"
                + "@RestController\n"
                + "public class UserController {\n"
                + "  public String findUser() { return \"ok\"; }\n"
                + "}\n").getBytes(StandardCharsets.UTF_8));
        RecordingExecutor executor = new RecordingExecutor();
        Clock clock = Clock.fixed(Instant.parse("2026-10-13T10:15:30Z"), ZoneOffset.UTC);
        ExistingProjectImporter importer = new ExistingProjectImporter(new JavaAstIndexer(),
                new DependencyGraphBuilder(), executor, clock);

        ProjectImportResult result = importer.importProject(new ProjectImportRequest(root, "Demo App"));

        assertEquals("forgeia/import-demo-app-20261013101530", result.getSnapshotBranch());
        assertEquals(0, result.getBaseBuild().getExitCode());
        assertTrue(result.getSummary().getEndpoints().contains("UserController"));
        assertTrue(result.getGraph().getNodes().contains("UserController"));
        assertTrue(Files.exists(result.getSummaryPath()));
        assertEquals(2, executor.commands.size());
        assertEquals("git branch forgeia/import-demo-app-20261013101530", executor.commands.get(0));
        assertEquals("mvn -q test", executor.commands.get(1));
    }

    private static final class RecordingExecutor implements ProjectCommandExecutor {
        private final List<String> commands = new ArrayList<String>();

        public CommandResult run(Path projectRoot, List<String> command) {
            commands.add(String.join(" ", command));
            return new CommandResult(0, "ok", false);
        }
    }
}
