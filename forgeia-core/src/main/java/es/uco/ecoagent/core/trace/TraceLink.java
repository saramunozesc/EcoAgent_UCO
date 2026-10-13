package es.uco.ecoagent.core.trace;

import java.time.Instant;

/**
 * Represents one auditable relation between two ForgeIA artifacts.
 */
public final class TraceLink {
    private final TraceElementType fromType;
    private final String fromId;
    private final TraceElementType toType;
    private final String toId;
    private final String relation;
    private final Instant createdAt;

    /**
     * Creates an immutable trace link.
     */
    public TraceLink(TraceElementType fromType, String fromId, TraceElementType toType, String toId,
            String relation, Instant createdAt) {
        this.fromType = fromType;
        this.fromId = fromId;
        this.toType = toType;
        this.toId = toId;
        this.relation = relation;
        this.createdAt = createdAt;
    }

    /**
     * Returns the source artifact type.
     */
    public TraceElementType getFromType() {
        return fromType;
    }

    /**
     * Returns the source artifact identifier.
     */
    public String getFromId() {
        return fromId;
    }

    /**
     * Returns the target artifact type.
     */
    public TraceElementType getToType() {
        return toType;
    }

    /**
     * Returns the target artifact identifier.
     */
    public String getToId() {
        return toId;
    }

    /**
     * Returns the semantic relation name.
     */
    public String getRelation() {
        return relation;
    }

    /**
     * Returns the creation timestamp.
     */
    public Instant getCreatedAt() {
        return createdAt;
    }
}
