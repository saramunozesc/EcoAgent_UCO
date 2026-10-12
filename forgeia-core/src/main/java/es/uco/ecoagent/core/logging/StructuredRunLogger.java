package es.uco.ecoagent.core.logging;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Writes structured JSONL events for each ForgeIA execution.
 */
public final class StructuredRunLogger {
    private final Path logFile;

    /**
     * Ensures the log directory exists and resolves the run log file.
     */
    public StructuredRunLogger(Path logsRoot, String runId) {
        try {
            Files.createDirectories(logsRoot);
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot create logs directory: " + logsRoot, exception);
        }
        this.logFile = logsRoot.resolve(runId + ".jsonl");
    }

    /**
     * Appends a structured event with timestamp, runId and custom fields.
     */
    public void event(String runId, String eventType, Map<String, String> fields) {
        Map<String, String> event = new LinkedHashMap<String, String>();
        event.put("timestamp", Instant.now().toString());
        event.put("runId", runId);
        event.put("event", eventType);
        event.putAll(fields);

        try (BufferedWriter writer = Files.newBufferedWriter(
                logFile,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {
            writer.write(toJson(event));
            writer.newLine();
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot write run log: " + logFile, exception);
        }
    }

    /**
     * Returns the path of the JSONL file receiving run events.
     */
    public Path getLogFile() {
        return logFile;
    }

    /**
     * Serializes a simple string map to a compact JSON object.
     */
    private static String toJson(Map<String, String> event) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        boolean first = true;
        for (Map.Entry<String, String> entry : event.entrySet()) {
            if (!first) {
                builder.append(",");
            }
            first = false;
            builder.append("\"").append(escape(entry.getKey())).append("\":");
            builder.append("\"").append(escape(entry.getValue())).append("\"");
        }
        builder.append("}");
        return builder.toString();
    }

    /**
     * Escapes the characters needed for the logger's simple JSON output.
     */
    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}

