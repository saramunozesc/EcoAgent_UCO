package es.uco.ecoagent.analysis.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * In-memory dependency graph for the MVP context compiler.
 */
public final class ProjectGraph {
    private final Set<String> nodes = new LinkedHashSet<String>();
    private final List<DependencyEdge> edges = new ArrayList<DependencyEdge>();

    /**
     * Adds a node if it does not already exist.
     */
    public void addNode(String node) {
        nodes.add(node);
    }

    /**
     * Adds a typed edge and ensures both nodes exist.
     */
    public void addEdge(String from, String to, String type) {
        addNode(from);
        addNode(to);
        edges.add(new DependencyEdge(from, to, type));
    }

    /**
     * Returns immutable graph nodes.
     */
    public Set<String> getNodes() {
        return Collections.unmodifiableSet(nodes);
    }

    /**
     * Returns immutable graph edges.
     */
    public List<DependencyEdge> getEdges() {
        return Collections.unmodifiableList(edges);
    }

    /**
     * Returns whether a node is directly connected to another node.
     */
    public boolean isAdjacent(String first, String second) {
        for (DependencyEdge edge : edges) {
            if ((edge.getFrom().equals(first) && edge.getTo().equals(second))
                    || (edge.getFrom().equals(second) && edge.getTo().equals(first))) {
                return true;
            }
        }
        return false;
    }
}

