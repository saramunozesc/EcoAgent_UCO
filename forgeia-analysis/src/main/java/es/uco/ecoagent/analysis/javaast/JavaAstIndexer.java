package es.uco.ecoagent.analysis.javaast;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Builds lightweight Java summaries without invoking an LLM.
 */
public final class JavaAstIndexer {
    private static final Pattern PACKAGE = Pattern.compile("^\\s*package\\s+([A-Za-z0-9_.]+)\\s*;");
    private static final Pattern IMPORT = Pattern.compile("^\\s*import\\s+(?:static\\s+)?([A-Za-z0-9_.]+)\\s*;");
    private static final Pattern TYPE = Pattern.compile("\\b(class|interface|enum)\\s+([A-Za-z0-9_]+)(?:\\s+implements\\s+([^\\{]+))?");
    private static final Pattern ANNOTATION = Pattern.compile("^\\s*@([A-Za-z0-9_]+)");
    private static final Pattern METHOD = Pattern.compile("\\b(public|protected|private)\\s+[^=;{}]+\\s+([A-Za-z0-9_]+)\\s*\\(([^)]*)\\)\\s*(?:throws\\s+[^{]+)?\\{?");

    /**
     * Indexes every Java file below a workspace root.
     */
    public List<JavaFileSummary> index(Path workspaceRoot) {
        List<JavaFileSummary> summaries = new ArrayList<JavaFileSummary>();
        try {
            Files.walk(workspaceRoot)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .forEach(path -> summaries.add(indexFile(workspaceRoot, path)));
            return summaries;
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot index Java workspace: " + workspaceRoot, exception);
        }
    }

    /**
     * Builds a structured summary for one Java file.
     */
    public JavaFileSummary indexFile(Path workspaceRoot, Path file) {
        try {
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            String packageName = "";
            String primaryType = file.getFileName().toString().replace(".java", "");
            Set<String> dependencies = new LinkedHashSet<String>();
            List<String> implementedTypes = new ArrayList<String>();
            List<String> annotations = new ArrayList<String>();
            List<JavaMethodSummary> methods = new ArrayList<JavaMethodSummary>();

            for (int index = 0; index < lines.size(); index++) {
                String line = lines.get(index);
                Matcher packageMatcher = PACKAGE.matcher(line);
                if (packageMatcher.find()) {
                    packageName = packageMatcher.group(1);
                }
                Matcher importMatcher = IMPORT.matcher(line);
                if (importMatcher.find()) {
                    dependencies.add(simpleName(importMatcher.group(1)));
                }
                Matcher annotationMatcher = ANNOTATION.matcher(line);
                if (annotationMatcher.find()) {
                    annotations.add(annotationMatcher.group(1));
                }
                Matcher typeMatcher = TYPE.matcher(line);
                if (typeMatcher.find()) {
                    primaryType = typeMatcher.group(2);
                    if (typeMatcher.group(3) != null) {
                        for (String item : typeMatcher.group(3).split(",")) {
                            implementedTypes.add(item.trim());
                            dependencies.add(item.trim());
                        }
                    }
                }
                Matcher methodMatcher = METHOD.matcher(line);
                if (methodMatcher.find() && !methodMatcher.group(2).equals(primaryType)) {
                    methods.add(new JavaMethodSummary(methodMatcher.group(2), compactSignature(line), index + 1));
                }
            }
            return new JavaFileSummary(relative(workspaceRoot, file), packageName, primaryType, implementedTypes, new ArrayList<String>(dependencies), annotations, methods);
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot index Java file: " + file, exception);
        }
    }

    /**
     * Converts an import to its simple type name.
     */
    private static String simpleName(String qualifiedName) {
        int dot = qualifiedName.lastIndexOf('.');
        return dot >= 0 ? qualifiedName.substring(dot + 1) : qualifiedName;
    }

    /**
     * Returns a normalized method signature line.
     */
    private static String compactSignature(String line) {
        return line.trim().replace("{", "").replaceAll("\\s+", " ");
    }

    /**
     * Returns a normalized workspace-relative path.
     */
    private static String relative(Path root, Path file) {
        return root.toAbsolutePath().normalize().relativize(file.toAbsolutePath().normalize()).toString().replace("\\", "/");
    }
}

