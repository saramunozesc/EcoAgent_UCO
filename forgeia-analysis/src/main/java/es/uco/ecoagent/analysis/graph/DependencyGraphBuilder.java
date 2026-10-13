package es.uco.ecoagent.analysis.graph;

import es.uco.ecoagent.analysis.javaast.JavaFileSummary;

import java.util.List;

/**
 * Builds an in-memory graph from Java file summaries.
 */
public final class DependencyGraphBuilder {
    /**
     * Adds file, symbol and dependency nodes with uses and implements edges.
     */
    public ProjectGraph build(List<JavaFileSummary> summaries) {
        ProjectGraph graph = new ProjectGraph();
        for (JavaFileSummary summary : summaries) {
            graph.addNode(summary.getPath());
            graph.addEdge(summary.getPath(), summary.getPrimaryType(), "declares");
            for (String dependency : summary.getDependencies()) {
                graph.addEdge(summary.getPrimaryType(), dependency, "uses");
            }
            for (String implementedType : summary.getImplementedTypes()) {
                graph.addEdge(summary.getPrimaryType(), implementedType, "implements");
            }
        }
        return graph;
    }
}

