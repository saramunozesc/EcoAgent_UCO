package es.uco.ecoagent.core.logging;

import java.util.UUID;

public final class RunId {
    private RunId() {
    }

    public static String create() {
        return "run-" + UUID.randomUUID().toString();
    }
}

