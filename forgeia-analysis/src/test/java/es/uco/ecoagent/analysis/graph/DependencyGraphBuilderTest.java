package es.uco.ecoagent.analysis.graph;

import es.uco.ecoagent.analysis.javaast.JavaAstIndexer;
import es.uco.ecoagent.analysis.javaast.JavaFileSummary;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import static org.junit.Assert.assertTrue;

/**
 * Verifies graph construction from Java summaries.
 */
public class DependencyGraphBuilderTest {
    /**
     * Ensures uses and declares edges are present.
     */
    @Test
    public void buildsDependencyGraph() throws Exception {
        Path root = Files.createTempDirectory("graph-index");
        Path file = root.resolve("ScoreService.java");
        Files.write(file, ("import demo.ScoreRepository;\npublic class ScoreService { public void save() {} }\n").getBytes("UTF-8"));
        JavaFileSummary summary = new JavaAstIndexer().indexFile(root, file);

        ProjectGraph graph = new DependencyGraphBuilder().build(Collections.singletonList(summary));

        assertTrue(graph.getNodes().contains("ScoreService"));
        assertTrue(graph.isAdjacent("ScoreService.java", "ScoreService"));
        assertTrue(graph.isAdjacent("ScoreService", "ScoreRepository"));
    }
}

