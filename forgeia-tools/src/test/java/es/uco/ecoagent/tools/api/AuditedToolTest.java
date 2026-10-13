package es.uco.ecoagent.tools.api;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Verifies structured auditing around tool executions.
 */
public final class AuditedToolTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    /**
     * Records sanitized input, result, duration and affected files.
     */
    @Test
    public void recordsSuccessfulToolExecution() {
        InMemoryToolExecutionAuditSink sink = new InMemoryToolExecutionAuditSink();
        Clock clock = Clock.fixed(Instant.parse("2026-10-13T12:00:00Z"), ZoneOffset.UTC);
        AuditedTool<String, String> tool = new AuditedTool<String, String>(new EchoTool(), sink,
                new AffectedFilesExtractor<String>() {
                    public List<String> extract(String output) {
                        return Arrays.asList("src/App.java");
                    }
                }, clock);

        String output = tool.execute(new ToolExecutionContext(workspace()), "token=secret-value");

        assertEquals("ok-token=secret-value", output);
        assertEquals(1, sink.getRecords().size());
        ToolExecutionRecord record = sink.getRecords().get(0);
        assertEquals("echo", record.getToolName());
        assertTrue(record.getSanitizedInput().contains("token=***"));
        assertEquals("src/App.java", record.getAffectedFiles().get(0));
        assertEquals("", record.getError());
    }

    /**
     * Records errors before rethrowing tool failures.
     */
    @Test
    public void recordsFailedToolExecution() {
        InMemoryToolExecutionAuditSink sink = new InMemoryToolExecutionAuditSink();
        AuditedTool<String, String> tool = new AuditedTool<String, String>(new FailingTool(), sink);

        try {
            tool.execute(new ToolExecutionContext(workspace()), "input");
        } catch (IllegalStateException expected) {
            assertEquals("boom", expected.getMessage());
        }

        assertEquals(1, sink.getRecords().size());
        assertEquals("boom", sink.getRecords().get(0).getError());
    }

    /**
     * Returns a temporary workspace path.
     */
    private Path workspace() {
        return temporaryFolder.getRoot().toPath();
    }

    private static final class EchoTool implements Tool<String, String> {
        public String name() {
            return "echo";
        }

        public String execute(ToolExecutionContext context, String input) {
            return "ok-" + input;
        }
    }

    private static final class FailingTool implements Tool<String, String> {
        public String name() {
            return "fail";
        }

        public String execute(ToolExecutionContext context, String input) {
            throw new IllegalStateException("boom");
        }
    }
}
