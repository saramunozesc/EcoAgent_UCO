package es.uco.ecoagent.tools.api;

/**
 * Receives structured audit records for tool executions.
 */
public interface ToolExecutionAuditSink {
    /**
     * Stores or forwards one tool execution record.
     */
    void record(ToolExecutionRecord record);
}
