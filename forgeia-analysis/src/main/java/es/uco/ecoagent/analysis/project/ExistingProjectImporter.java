package es.uco.ecoagent.analysis.project;

import es.uco.ecoagent.analysis.graph.DependencyGraphBuilder;
import es.uco.ecoagent.analysis.graph.ProjectGraph;
import es.uco.ecoagent.analysis.javaast.JavaAstIndexer;
import es.uco.ecoagent.analysis.javaast.JavaFileSummary;
import es.uco.ecoagent.tools.command.CommandResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Imports an existing Maven project before ForgeIA proposes changes.
 */
public final class ExistingProjectImporter {
    private final JavaAstIndexer indexer;
    private final DependencyGraphBuilder graphBuilder;
    private final ProjectCommandExecutor commandExecutor;
    private final Clock clock;

    /**
     * Creates an importer with default deterministic analyzers and command runner.
     */
    public ExistingProjectImporter() {
        this(new JavaAstIndexer(), new DependencyGraphBuilder(), new DefaultProjectCommandExecutor(), Clock.systemUTC());
    }

    /**
     * Creates an importer with injected collaborators for tests.
     */
    public ExistingProjectImporter(JavaAstIndexer indexer, DependencyGraphBuilder graphBuilder,
            ProjectCommandExecutor commandExecutor, Clock clock) {
        this.indexer = indexer;
        this.graphBuilder = graphBuilder;
        this.commandExecutor = commandExecutor;
        this.clock = clock;
    }

    /**
     * Validates, snapshots, scans, indexes and builds an existing Maven project.
     */
    public ProjectImportResult importProject(ProjectImportRequest request) {
        Path root = request.getProjectRoot().toAbsolutePath().normalize();
        requireSupportedProject(root);

        String branchName = "forgeia/import-" + sanitize(request.getProjectName()) + "-"
                + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(clock.getZone()).format(clock.instant());
        CommandResult snapshot = commandExecutor.run(root, Arrays.asList("git", "branch", branchName));
        if (snapshot.getExitCode() != 0) {
            throw new IllegalStateException("Cannot create snapshot branch: " + snapshot.getOutput());
        }

        ScanResult scan = scan(root);
        List<JavaFileSummary> summaries = indexer.index(root);
        ProjectGraph graph = graphBuilder.build(summaries);
        CommandResult baseBuild = commandExecutor.run(root, Arrays.asList("mvn", "-q", "test"));

        ProjectSummary summary = buildSummary(request.getProjectName(), scan, summaries);
        Path summaryPath = persistSummary(root, summary, baseBuild, branchName);
        return new ProjectImportResult(summary, graph, branchName, baseBuild, summaryPath);
    }

    /**
     * Fails fast when the directory is not a supported Maven project.
     */
    private static void requireSupportedProject(Path root) {
        if (!Files.isDirectory(root) || !Files.exists(root.resolve("pom.xml"))) {
            throw new IllegalArgumentException("Supported Maven project must contain pom.xml: " + root);
        }
    }

    /**
     * Scans files and Maven module roots.
     */
    private static ScanResult scan(Path root) {
        try {
            List<Path> files = new ArrayList<Path>();
            List<String> modules = new ArrayList<String>();
            Files.walk(root).filter(Files::isRegularFile).forEach(path -> {
                files.add(path);
                if (path.getFileName().toString().equals("pom.xml")) {
                    modules.add(relative(root, path.getParent()));
                }
            });
            return new ScanResult(files.size(), modules);
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot scan project: " + root, exception);
        }
    }

    /**
     * Extracts endpoints, entities and dependencies from Java summaries.
     */
    private static ProjectSummary buildSummary(String projectName, ScanResult scan, List<JavaFileSummary> summaries) {
        List<String> endpoints = new ArrayList<String>();
        List<String> entities = new ArrayList<String>();
        Set<String> dependencies = new LinkedHashSet<String>();
        for (JavaFileSummary summary : summaries) {
            dependencies.addAll(summary.getDependencies());
            if (summary.getAnnotations().contains("RestController") || summary.getAnnotations().contains("Controller")) {
                endpoints.add(summary.getPrimaryType());
            }
            if (summary.getAnnotations().contains("Entity")) {
                entities.add(summary.getPrimaryType());
            }
        }
        return new ProjectSummary(projectName, scan.fileCount, summaries.size(), scan.modules, endpoints, entities,
                new ArrayList<String>(dependencies), summaries);
    }

    /**
     * Writes a compact JSON summary under .forgeia.
     */
    private static Path persistSummary(Path root, ProjectSummary summary, CommandResult baseBuild, String branchName) {
        try {
            Path directory = root.resolve(".forgeia");
            Files.createDirectories(directory);
            Path output = directory.resolve("project-summary.json");
            Files.write(output, toJson(summary, baseBuild, branchName).getBytes(StandardCharsets.UTF_8));
            return output;
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot persist project summary", exception);
        }
    }

    /**
     * Serializes only stable import metadata needed for later phases.
     */
    private static String toJson(ProjectSummary summary, CommandResult baseBuild, String branchName) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        appendField(builder, "projectName", summary.getProjectName()).append(",\n");
        appendField(builder, "snapshotBranch", branchName).append(",\n");
        builder.append("  \"fileCount\": ").append(summary.getFileCount()).append(",\n");
        builder.append("  \"javaFileCount\": ").append(summary.getJavaFileCount()).append(",\n");
        builder.append("  \"baseBuildExitCode\": ").append(baseBuild.getExitCode()).append(",\n");
        appendArray(builder, "modules", summary.getModules()).append(",\n");
        appendArray(builder, "endpoints", summary.getEndpoints()).append(",\n");
        appendArray(builder, "entities", summary.getEntities()).append(",\n");
        appendArray(builder, "dependencies", summary.getDependencies()).append("\n");
        builder.append("}\n");
        return builder.toString();
    }

    /**
     * Appends a JSON string field.
     */
    private static StringBuilder appendField(StringBuilder builder, String name, String value) {
        return builder.append("  \"").append(name).append("\": \"").append(escape(value)).append("\"");
    }

    /**
     * Appends a JSON string array field.
     */
    private static StringBuilder appendArray(StringBuilder builder, String name, List<String> values) {
        builder.append("  \"").append(name).append("\": [");
        for (int index = 0; index < values.size(); index++) {
            if (index > 0) {
                builder.append(", ");
            }
            builder.append("\"").append(escape(values.get(index))).append("\"");
        }
        return builder.append("]");
    }

    /**
     * Escapes a string for the small JSON writer.
     */
    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    /**
     * Returns a lowercase branch-safe project name.
     */
    private static String sanitize(String value) {
        return value.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
    }

    /**
     * Returns a normalized project-relative path.
     */
    private static String relative(Path root, Path path) {
        if (root.equals(path)) {
            return ".";
        }
        return root.relativize(path).toString().replace("\\", "/");
    }

    private static final class ScanResult {
        private final int fileCount;
        private final List<String> modules;

        private ScanResult(int fileCount, List<String> modules) {
            this.fileCount = fileCount;
            this.modules = modules;
        }
    }
}
