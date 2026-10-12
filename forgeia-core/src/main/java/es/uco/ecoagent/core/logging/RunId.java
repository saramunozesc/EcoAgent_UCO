package es.uco.ecoagent.core.logging;

import java.util.UUID;

/**
 * Generates unique identifiers for auditable ForgeIA executions.
 */
public final class RunId {
    /**
     * Prevents instantiation because run IDs are created statically.
     */
    private RunId() {
    }

    /**
     * Creates a run identifier with a stable prefix and random UUID.
     */
    public static String create() {
        return "run-" + UUID.randomUUID().toString();
    }
}

