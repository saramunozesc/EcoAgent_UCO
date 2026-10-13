package es.uco.ecoagent.analysis.project;

import es.uco.ecoagent.analysis.graph.ProjectGraph;
import es.uco.ecoagent.tools.command.CommandResult;

import java.nio.file.Path;

/**
 * Reports the deterministic artifacts produced by existing-project import.
 */
public final class ProjectImportResult {
    private final ProjectSummary summary;
    private final ProjectGraph graph;
    private final String snapshotBranch;
    private final CommandResult baseBuild;
    private final Path summaryPath;

    /**
     * Creates an immutable import result.
     */
    public ProjectImportResult(ProjectSummary summary, ProjectGraph graph, String snapshotBranch,
            CommandResult baseBuild, Path summaryPath) {
        this.summary = summary;
        this.graph = graph;
        this.snapshotBranch = snapshotBranch;
        this.baseBuild = baseBuild;
        this.summaryPath = summaryPath;
    }

    /**
     * Returns the compact project summary.
     */
    public ProjectSummary getSummary() {
        return summary;
    }

    /**
     * Returns the dependency graph built from Java summaries.
     */
    public ProjectGraph getGraph() {
        return graph;
    }

    /**
     * Returns the branch created as a pre-change snapshot.
     */
    public String getSnapshotBranch() {
        return snapshotBranch;
    }

    /**
     * Returns the Maven test result captured before modifications.
     */
    public CommandResult getBaseBuild() {
        return baseBuild;
    }

    /**
     * Returns the persisted project summary path.
     */
    public Path getSummaryPath() {
        return summaryPath;
    }
}
