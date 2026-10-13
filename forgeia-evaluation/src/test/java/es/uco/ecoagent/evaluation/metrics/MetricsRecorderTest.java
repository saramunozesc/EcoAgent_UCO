package es.uco.ecoagent.evaluation.metrics;

import es.uco.ecoagent.core.domain.LlmUsage;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.Assert.assertTrue;

/**
 * Verifies JSONL metric recording for phase 9.
 */
public final class MetricsRecorderTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    /**
     * Records LLM usage and validation metrics with trace identifiers.
     */
    @Test
    public void writesMetricsAsJsonl() throws Exception {
        Path file = temporaryFolder.newFile("metrics.jsonl").toPath();
        Clock clock = Clock.fixed(Instant.parse("2026-10-13T12:00:00Z"), ZoneOffset.UTC);
        MetricsRecorder recorder = new MetricsRecorder(file, clock);

        recorder.recordLlmCall("mathkids", "REQ-014", "TASK-014-3", "RUN-1",
                new LlmUsage("fake", "model-a", 1000, 250, 1200L, new BigDecimal("0.015")));
        recorder.recordValidation("mathkids", "REQ-014", "TASK-014-3", "RUN-1", true, 18, 0);

        String content = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
        assertTrue(content.contains("\"name\":\"INPUT_TOKENS\""));
        assertTrue(content.contains("\"name\":\"ESTIMATED_COST\""));
        assertTrue(content.contains("\"name\":\"BUILD_SUCCESS\""));
        assertTrue(content.contains("\"taskId\":\"TASK-014-3\""));
        assertTrue(content.contains("2026-10-13T12:00:00Z"));
    }
}
