package es.uco.ecoagent.tools.fs;

/**
 * Structured result containing text read from a workspace file.
 */
public final class ReadFileResult {
    private final String text;

    /**
     * Stores the file text selected by readFile.
     */
    public ReadFileResult(String text) {
        this.text = text;
    }

    /**
     * Returns the read text.
     */
    public String getText() {
        return text;
    }
}

