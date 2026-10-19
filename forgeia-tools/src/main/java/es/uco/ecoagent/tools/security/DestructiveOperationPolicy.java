package es.uco.ecoagent.tools.security;

import es.uco.ecoagent.tools.api.ToolException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Denies destructive operations unless the orchestrator supplies an explicit,
 * operation-specific approval obtained from the user.
 */
public final class DestructiveOperationPolicy {
    private final Set<String> approvedOperationIds;

    private DestructiveOperationPolicy(Set<String> approvedOperationIds) {
        this.approvedOperationIds = Collections.unmodifiableSet(
                new HashSet<String>(approvedOperationIds));
    }

    /**
     * Creates the safe default used by ordinary tool calls.
     */
    public static DestructiveOperationPolicy denyAll() {
        return new DestructiveOperationPolicy(Collections.<String>emptySet());
    }

    /**
     * Creates a policy from approvals recorded by the human-facing layer.
     */
    public static DestructiveOperationPolicy withApprovals(Set<String> approvedOperationIds) {
        return new DestructiveOperationPolicy(approvedOperationIds);
    }

    /**
     * Fails unless the exact operation identifier was explicitly approved.
     */
    public void requireApproved(String operationId, String target) {
        if (operationId == null || operationId.trim().isEmpty()) {
            throw new ToolException("Destructive operation id is required");
        }
        if (!approvedOperationIds.contains(operationId)) {
            throw new ToolException("Human approval required for " + operationId + " on " + target);
        }
    }
}
