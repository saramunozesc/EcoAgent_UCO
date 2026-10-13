package es.uco.ecoagent.llm.code;

import es.uco.ecoagent.llm.requirements.RequirementValidationException;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses the small JSON shape expected from the first code agent.
 */
final class PatchProposalParser {
    /**
     * Prevents instantiation because parsing is static.
     */
    private PatchProposalParser() {
    }

    /**
     * Parses a patch proposal JSON response.
     */
    static PatchProposal parse(String json) {
        String taskId = requiredString(json, "taskId");
        String summary = requiredString(json, "summary");
        String unifiedDiff = requiredString(json, "unifiedDiff");
        List<String> files = requiredStringArray(json, "files");
        if (taskId.trim().isEmpty() || unifiedDiff.trim().isEmpty()) {
            throw new RequirementValidationException("Patch proposal must include taskId and unifiedDiff");
        }
        return new PatchProposal(taskId, summary, unifiedDiff, files);
    }

    /**
     * Extracts a required string value from a flat JSON object.
     */
    private static String requiredString(String json, String key) {
        String marker = "\"" + key + "\"";
        int keyIndex = json.indexOf(marker);
        if (keyIndex < 0) {
            throw new RequirementValidationException("Missing field: " + key);
        }
        int colon = json.indexOf(':', keyIndex + marker.length());
        int start = json.indexOf('"', colon + 1);
        if (colon < 0 || start < 0) {
            throw new RequirementValidationException("Invalid string field: " + key);
        }
        return readString(json, start);
    }

    /**
     * Extracts a required array of strings from a flat JSON object.
     */
    private static List<String> requiredStringArray(String json, String key) {
        String marker = "\"" + key + "\"";
        int keyIndex = json.indexOf(marker);
        if (keyIndex < 0) {
            throw new RequirementValidationException("Missing field: " + key);
        }
        int open = json.indexOf('[', keyIndex + marker.length());
        int close = json.indexOf(']', open + 1);
        if (open < 0 || close < 0) {
            throw new RequirementValidationException("Invalid array field: " + key);
        }
        List<String> values = new ArrayList<String>();
        int index = open + 1;
        while (index < close) {
            int quote = json.indexOf('"', index);
            if (quote < 0 || quote > close) {
                break;
            }
            values.add(readString(json, quote));
            index = nextStringEnd(json, quote) + 1;
        }
        return values;
    }

    /**
     * Reads a JSON string starting at its opening quote.
     */
    private static String readString(String json, int startQuote) {
        StringBuilder builder = new StringBuilder();
        for (int index = startQuote + 1; index < json.length(); index++) {
            char current = json.charAt(index);
            if (current == '"') {
                return builder.toString();
            }
            if (current == '\\' && index + 1 < json.length()) {
                char escaped = json.charAt(++index);
                if (escaped == 'n') {
                    builder.append('\n');
                } else {
                    builder.append(escaped);
                }
            } else {
                builder.append(current);
            }
        }
        throw new RequirementValidationException("Unclosed JSON string");
    }

    /**
     * Finds the closing quote for a JSON string.
     */
    private static int nextStringEnd(String json, int startQuote) {
        boolean escaped = false;
        for (int index = startQuote + 1; index < json.length(); index++) {
            char current = json.charAt(index);
            if (escaped) {
                escaped = false;
            } else if (current == '\\') {
                escaped = true;
            } else if (current == '"') {
                return index;
            }
        }
        throw new RequirementValidationException("Unclosed JSON string");
    }
}

