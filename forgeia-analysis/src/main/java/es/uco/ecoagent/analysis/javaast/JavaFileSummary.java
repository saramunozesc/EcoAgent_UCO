package es.uco.ecoagent.analysis.javaast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Structured AST-like summary for one Java source file.
 */
public final class JavaFileSummary {
    private final String path;
    private final String packageName;
    private final String primaryType;
    private final List<String> implementedTypes;
    private final List<String> dependencies;
    private final List<String> annotations;
    private final List<JavaMethodSummary> methods;

    /**
     * Creates an immutable source file summary.
     */
    public JavaFileSummary(
            String path,
            String packageName,
            String primaryType,
            List<String> implementedTypes,
            List<String> dependencies,
            List<String> annotations,
            List<JavaMethodSummary> methods) {
        this.path = path;
        this.packageName = packageName;
        this.primaryType = primaryType;
        this.implementedTypes = Collections.unmodifiableList(new ArrayList<String>(implementedTypes));
        this.dependencies = Collections.unmodifiableList(new ArrayList<String>(dependencies));
        this.annotations = Collections.unmodifiableList(new ArrayList<String>(annotations));
        this.methods = Collections.unmodifiableList(new ArrayList<JavaMethodSummary>(methods));
    }

    /**
     * Returns the workspace-relative Java file path.
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns the declared package name.
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Returns the main class or interface name.
     */
    public String getPrimaryType() {
        return primaryType;
    }

    /**
     * Returns interfaces implemented by the primary type.
     */
    public List<String> getImplementedTypes() {
        return implementedTypes;
    }

    /**
     * Returns imported or referenced dependency names.
     */
    public List<String> getDependencies() {
        return dependencies;
    }

    /**
     * Returns annotations found in the file.
     */
    public List<String> getAnnotations() {
        return annotations;
    }

    /**
     * Returns method signatures found in the file.
     */
    public List<JavaMethodSummary> getMethods() {
        return methods;
    }

    /**
     * Returns a compact summary suitable for L1 context.
     */
    public String toSummaryText() {
        StringBuilder builder = new StringBuilder();
        builder.append(primaryType).append("\n");
        builder.append("  implements: ").append(implementedTypes).append("\n");
        builder.append("  dependencies: ").append(dependencies).append("\n");
        builder.append("  methods:");
        for (JavaMethodSummary method : methods) {
            builder.append("\n    ").append(method.getSignature());
        }
        return builder.toString();
    }
}

