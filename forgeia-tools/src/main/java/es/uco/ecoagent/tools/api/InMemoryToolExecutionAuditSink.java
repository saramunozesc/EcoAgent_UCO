package es.uco.ecoagent.tools.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Keeps tool audit records in memory for tests and local runs.
 */
public final class InMemoryToolExecutionAuditSink implements ToolExecutionAuditSink {
    private final List<ToolExecutionRecord> records = new ArrayList<ToolExecutionRecord>();

    /**
     * Stores one audit record.
     */
    public void record(ToolExecutionRecord record) {
        records.add(record);
    }

    /**
     * Returns immutable records in execution order.
     */
    public List<ToolExecutionRecord> getRecords() {
        return Collections.unmodifiableList(records);
    }
}
