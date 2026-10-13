package es.uco.ecoagent.core.trace;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.Assert.assertTrue;

/**
 * Verifies JSONL traceability persistence.
 */
public final class TraceStoreTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    /**
     * Stores links from requirements to tasks and related effects.
     */
    @Test
    public void persistsTraceLink() throws Exception {
        Path traceFile = temporaryFolder.newFile("traceability.jsonl").toPath();
        Clock clock = Clock.fixed(Instant.parse("2026-10-13T11:00:00Z"), ZoneOffset.UTC);
        TraceStore store = new TraceStore(traceFile, clock);

        store.link(TraceElementType.REQUIREMENT, "REQ-001", TraceElementType.TASK, "TASK-001-1", "plans");

        String content = new String(Files.readAllBytes(traceFile), StandardCharsets.UTF_8);
        assertTrue(content.contains("\"fromType\":\"REQUIREMENT\""));
        assertTrue(content.contains("\"toType\":\"TASK\""));
        assertTrue(content.contains("\"relation\":\"plans\""));
        assertTrue(content.contains("2026-10-13T11:00:00Z"));
    }
}
