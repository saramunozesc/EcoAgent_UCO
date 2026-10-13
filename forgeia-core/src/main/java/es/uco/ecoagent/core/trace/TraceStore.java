package es.uco.ecoagent.core.trace;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;

/**
 * Persists JSONL traceability links between requirements, tasks and effects.
 */
public final class TraceStore {
    private final Path traceFile;
    private final Clock clock;

    /**
     * Creates a store rooted at a workspace directory.
     */
    public TraceStore(Path workspaceRoot) {
        this(workspaceRoot.resolve("traceability.jsonl"), Clock.systemUTC());
    }

    /**
     * Creates a store with an injected clock for tests.
     */
    public TraceStore(Path traceFile, Clock clock) {
        this.traceFile = traceFile;
        this.clock = clock;
    }

    /**
     * Links a requirement to a task, diff, commit or test artifact.
     */
    public TraceLink link(TraceElementType fromType, String fromId, TraceElementType toType, String toId,
            String relation) {
        TraceLink link = new TraceLink(fromType, fromId, toType, toId, relation, Instant.now(clock));
        append(link);
        return link;
    }

    /**
     * Returns the JSONL file used for traceability.
     */
    public Path getTraceFile() {
        return traceFile;
    }

    /**
     * Appends one link as JSONL.
     */
    private void append(TraceLink link) {
        try {
            if (traceFile.getParent() != null) {
                Files.createDirectories(traceFile.getParent());
            }
            Files.write(traceFile, (toJson(link) + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
                    Files.exists(traceFile) ? java.nio.file.StandardOpenOption.APPEND : java.nio.file.StandardOpenOption.CREATE);
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot write traceability link", exception);
        }
    }

    /**
     * Serializes one trace link as compact JSON.
     */
    private static String toJson(TraceLink link) {
        return "{\"fromType\":\"" + link.getFromType() + "\",\"fromId\":\"" + escape(link.getFromId())
                + "\",\"toType\":\"" + link.getToType() + "\",\"toId\":\"" + escape(link.getToId())
                + "\",\"relation\":\"" + escape(link.getRelation()) + "\",\"createdAt\":\""
                + link.getCreatedAt() + "\"}";
    }

    /**
     * Escapes a string for the small JSONL writer.
     */
    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
