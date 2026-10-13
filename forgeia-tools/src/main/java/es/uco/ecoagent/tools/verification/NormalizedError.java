package es.uco.ecoagent.tools.verification;

/**
 * Compact error record passed to repair logic instead of full logs.
 */
public final class NormalizedError {
    private final ErrorType type;
    private final String file;
    private final int line;
    private final String message;
    private final String relatedSymbol;
    private final String testName;

    /**
     * Creates an immutable normalized error.
     */
    public NormalizedError(ErrorType type, String file, int line, String message, String relatedSymbol, String testName) {
        this.type = type;
        this.file = file;
        this.line = line;
        this.message = message;
        this.relatedSymbol = relatedSymbol;
        this.testName = testName;
    }

    /**
     * Returns the normalized error category.
     */
    public ErrorType getType() {
        return type;
    }

    /**
     * Returns the affected source file when known.
     */
    public String getFile() {
        return file;
    }

    /**
     * Returns the affected source line or -1 when unknown.
     */
    public int getLine() {
        return line;
    }

    /**
     * Returns the compact error message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns a related symbol when one can be inferred.
     */
    public String getRelatedSymbol() {
        return relatedSymbol;
    }

    /**
     * Returns the failing test name when known.
     */
    public String getTestName() {
        return testName;
    }
}

