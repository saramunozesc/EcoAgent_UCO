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
        return sanitizeText(String.valueOf(input), 500);
    }

    /**
     * Redacts common credential forms while retaining a bounded diagnostic.
     */
    public static String sanitizeText(String input, int maxLength) {
        if (input == null) {
            return "";
        }
        String value = input
                .replaceAll("(?i)(token|password|secret|api[_-]?key|access[_-]?key|private[_-]?key)(\\s*[=:]\\s*)[\\\"']?([^,\\s}\\\"']+)", "$1$2***")
                .replaceAll("(?i)Bearer\\s+[A-Za-z0-9._~+/-]+=*", "Bearer ***")
                .replaceAll("gh[pousr]_[A-Za-z0-9_]+", "github_***")
                .replaceAll("sk-[A-Za-z0-9_-]{12,}", "sk-***")
                .replaceAll("AKIA[0-9A-Z]{16}", "AKIA***")
                .replaceAll("(?s)-----BEGIN [A-Z ]*PRIVATE KEY-----.*?-----END [A-Z ]*PRIVATE KEY-----",
                        "-----PRIVATE KEY REDACTED-----");
        return value.length() <= maxLength ? value : value.substring(0, maxLength) + "...";
    }
}
