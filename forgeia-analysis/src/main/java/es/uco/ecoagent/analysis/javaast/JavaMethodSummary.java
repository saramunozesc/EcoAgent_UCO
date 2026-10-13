package es.uco.ecoagent.analysis.javaast;

/**
 * Summarizes one Java method signature found in a source file.
 */
public final class JavaMethodSummary {
    private final String name;
    private final String signature;
    private final int line;

    /**
     * Creates an immutable method summary.
     */
    public JavaMethodSummary(String name, String signature, int line) {
        this.name = name;
        this.signature = signature;
        this.line = line;
    }

    /**
     * Returns the method name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the compact method signature.
     */
    public String getSignature() {
        return signature;
    }

    /**
     * Returns the one-based source line.
     */
    public int getLine() {
        return line;
    }
}

