package es.uco.ecoagent.tools.api;

/**
 * Produces safe input summaries for tool audit logs.
 */
public final class InputSanitizer {
    private InputSanitizer() {
    }

    /**
     * Redacts common credential-like values and bounds length.
     */
    public static String sanitize(Object input) {
        if (input == null) {
            return "";
        }
        String value = String.valueOf(input)
                .replaceAll("(?i)(token|password|secret)=([^,\\s}]+)", "$1=***")
                .replaceAll("ghp_[A-Za-z0-9_]+", "ghp_***");
        return value.length() <= 500 ? value : value.substring(0, 500) + "...";
    }
}
