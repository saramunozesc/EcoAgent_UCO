package es.uco.ecoagent.tools.api;

import java.time.Clock;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * Wraps a tool and records input, duration, result, errors and files.
 */
public final class AuditedTool<I, O> implements Tool<I, O> {
    private final Tool<I, O> delegate;
    private final ToolExecutionAuditSink auditSink;
    private final AffectedFilesExtractor<O> affectedFilesExtractor;
    private final Clock clock;

    /**
     * Creates a wrapper with no affected-file extraction.
     */
    public AuditedTool(Tool<I, O> delegate, ToolExecutionAuditSink auditSink) {
        this(delegate, auditSink, new AffectedFilesExtractor<O>() {
            public List<String> extract(O output) {
                return Collections.emptyList();
            }
        }, Clock.systemUTC());
    }

    /**
     * Creates a wrapper with injectable extraction and clock.
     */
    public AuditedTool(Tool<I, O> delegate, ToolExecutionAuditSink auditSink,
            AffectedFilesExtractor<O> affectedFilesExtractor, Clock clock) {
        this.delegate = delegate;
        this.auditSink = auditSink;
        this.affectedFilesExtractor = affectedFilesExtractor;
        this.clock = clock;
    }

    /**
     * Returns the wrapped tool name.
     */
    public String name() {
        return delegate.name();
    }

    /**
     * Executes the tool and records success or failure.
     */
    public O execute(ToolExecutionContext context, I input) {
        long started = clock.millis();
        try {
            O output = delegate.execute(context, input);
            auditSink.record(record(input, output, "", affectedFilesExtractor.extract(output), started));
            return output;
        } catch (RuntimeException exception) {
            auditSink.record(record(input, null, exception.getMessage(), Collections.<String>emptyList(), started));
            throw exception;
        }
    }

    /**
     * Builds one compact audit record.
     */
    private ToolExecutionRecord record(I input, O output, String error, List<String> affectedFiles, long started) {
        return new ToolExecutionRecord(name(), InputSanitizer.sanitize(input), clock.millis() - started,
                output == null ? "" : InputSanitizer.sanitize(output), InputSanitizer.sanitize(error),
                affectedFiles, Instant.now(clock));
    }
}
