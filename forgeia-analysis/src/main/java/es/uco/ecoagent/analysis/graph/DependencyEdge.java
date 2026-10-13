package es.uco.ecoagent.analysis.graph;

/**
 * Represents a directed relation between two project graph nodes.
 */
public final class DependencyEdge {
    private final String from;
    private final String to;
    private final String type;

    /**
     * Creates an immutable dependency edge.
     */
    public DependencyEdge(String from, String to, String type) {
        this.from = from;
        this.to = to;
        this.type = type;
    }

    /**
     * Returns the source node.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the target node.
     */
    public String getTo() {
        return to;
    }

    /**
     * Returns the relation kind such as uses or implements.
     */
    public String getType() {
        return type;
    }
}

