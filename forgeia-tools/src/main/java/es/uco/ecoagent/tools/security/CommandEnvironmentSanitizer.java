package es.uco.ecoagent.tools.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Removes credentials from the environment inherited by generated builds.
 */
public final class CommandEnvironmentSanitizer {
    private CommandEnvironmentSanitizer() {
    }

    /**
     * Removes variables whose names conventionally carry credentials.
     */
    public static void removeSensitiveVariables(Map<String, String> environment) {
        List<String> names = new ArrayList<String>(environment.keySet());
        for (String name : names) {
            if (isSensitiveName(name)) {
                environment.remove(name);
            }
        }
    }

    static boolean isSensitiveName(String name) {
        String normalized = name.toUpperCase(Locale.ROOT);
        return normalized.contains("TOKEN")
                || normalized.contains("PASSWORD")
                || normalized.contains("SECRET")
                || normalized.contains("API_KEY")
                || normalized.contains("ACCESS_KEY")
                || normalized.contains("PRIVATE_KEY")
                || normalized.contains("CREDENTIAL")
                || normalized.endsWith("_AUTH");
    }
}
