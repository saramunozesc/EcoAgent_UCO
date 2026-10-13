package es.uco.ecoagent.analysis.project;

import es.uco.ecoagent.analysis.javaast.JavaFileSummary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Compact persistent view of an imported Maven project.
 */
public final class ProjectSummary {
    private final String projectName;
    private final int fileCount;
    private final int javaFileCount;
    private final List<String> modules;
    private final List<String> endpoints;
    private final List<String> entities;
    private final List<String> dependencies;
    private final List<JavaFileSummary> javaSummaries;

    /**
     * Creates an immutable summary for later planning and impact analysis.
     */
    public ProjectSummary(String projectName, int fileCount, int javaFileCount, List<String> modules,
            List<String> endpoints, List<String> entities, List<String> dependencies,
            List<JavaFileSummary> javaSummaries) {
        this.projectName = projectName;
        this.fileCount = fileCount;
        this.javaFileCount = javaFileCount;
        this.modules = Collections.unmodifiableList(new ArrayList<String>(modules));
        this.endpoints = Collections.unmodifiableList(new ArrayList<String>(endpoints));
        this.entities = Collections.unmodifiableList(new ArrayList<String>(entities));
        this.dependencies = Collections.unmodifiableList(new ArrayList<String>(dependencies));
        this.javaSummaries = Collections.unmodifiableList(new ArrayList<JavaFileSummary>(javaSummaries));
    }

    /**
     * Returns the imported project name.
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Returns the number of regular files scanned.
     */
    public int getFileCount() {
        return fileCount;
    }

    /**
     * Returns the number of Java files indexed.
     */
    public int getJavaFileCount() {
        return javaFileCount;
    }

    /**
     * Returns Maven module paths discovered from pom.xml files.
     */
    public List<String> getModules() {
        return modules;
    }

    /**
     * Returns controller-like types discovered from annotations.
     */
    public List<String> getEndpoints() {
        return endpoints;
    }

    /**
     * Returns entity-like types discovered from annotations.
     */
    public List<String> getEntities() {
        return entities;
    }

    /**
     * Returns unique imported type names across the project.
     */
    public List<String> getDependencies() {
        return dependencies;
    }

    /**
     * Returns Java summaries used by the context compiler.
     */
    public List<JavaFileSummary> getJavaSummaries() {
        return javaSummaries;
    }
}
