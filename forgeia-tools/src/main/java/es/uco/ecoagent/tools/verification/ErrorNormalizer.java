package es.uco.ecoagent.tools.verification;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extracts compact repair-relevant errors from Maven output.
 */
public final class ErrorNormalizer {
    private static final Pattern COMPILATION = Pattern.compile("\\[ERROR\\]\\s+(.+\\.java):\\[(\\d+),\\d+\\]\\s+(.+)");
    private static final Pattern TEST = Pattern.compile("\\[ERROR\\]\\s+([^\\s]+)\\s+Time elapsed:.*<<< (FAILURE|ERROR)!");
    private static final Pattern SYMBOL = Pattern.compile("symbol:\\s+(?:method|class|variable)\\s+([A-Za-z0-9_]+)");

    /**
     * Converts Maven output into a bounded list of normalized errors.
     */
    public List<NormalizedError> normalize(String output) {
        List<NormalizedError> errors = new ArrayList<NormalizedError>();
        String[] lines = output == null ? new String[0] : output.split("\\r?\\n");
        for (int index = 0; index < lines.length && errors.size() < 5; index++) {
            Matcher compilation = COMPILATION.matcher(lines[index]);
            if (compilation.find()) {
                errors.add(new NormalizedError(
                        ErrorType.COMPILATION_ERROR,
                        shortFile(compilation.group(1)),
                        Integer.parseInt(compilation.group(2)),
                        compilation.group(3).trim(),
                        findRelatedSymbol(lines, index),
                        null));
                continue;
            }
            Matcher test = TEST.matcher(lines[index]);
            if (test.find()) {
                errors.add(new NormalizedError(
                        ErrorType.TEST_FAILURE,
                        null,
                        -1,
                        "Test " + test.group(2).toLowerCase(),
                        null,
                        test.group(1)));
            }
        }
        if (errors.isEmpty() && output != null && output.trim().length() > 0) {
            errors.add(new NormalizedError(ErrorType.UNKNOWN_ERROR, null, -1, firstMeaningfulLine(output), null, null));
        }
        return errors;
    }

    /**
     * Finds a nearby compiler symbol line.
     */
    private static String findRelatedSymbol(String[] lines, int startIndex) {
        for (int index = startIndex; index < lines.length && index < startIndex + 6; index++) {
            Matcher matcher = SYMBOL.matcher(lines[index]);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }

    /**
     * Returns a file name without the full local path.
     */
    private static String shortFile(String file) {
        return file.replace("\\", "/").replaceAll("^.*/", "");
    }

    /**
     * Returns the first useful line as fallback.
     */
    private static String firstMeaningfulLine(String output) {
        String[] lines = output.split("\\r?\\n");
        for (String line : lines) {
            if (line.trim().length() > 0) {
                return line.trim();
            }
        }
        return "Unknown Maven error";
    }
}

