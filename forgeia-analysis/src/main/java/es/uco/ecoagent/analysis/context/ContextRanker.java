package es.uco.ecoagent.analysis.context;

import es.uco.ecoagent.analysis.graph.ProjectGraph;
import es.uco.ecoagent.analysis.javaast.JavaFileSummary;
import es.uco.ecoagent.core.planning.PlannedTask;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Scores context candidates using the initial weighted formula.
 */
public final class ContextRanker {
    /**
     * Calculates the weighted score for a task and source summary.
     */
    public double score(PlannedTask task, JavaFileSummary summary, ProjectGraph graph) {
        double semanticSimilarity = semanticSimilarity(task, summary);
        double dependencyProximity = dependencyProximity(task, summary, graph);
        double requirementRelation = requirementRelation(task, summary);
        double recentModification = recentModification(task, summary);
        return 0.40d * semanticSimilarity
                + 0.25d * dependencyProximity
                + 0.20d * requirementRelation
                + 0.15d * recentModification;
    }

    /**
     * Scores overlap between task words and source symbols.
     */
    private double semanticSimilarity(PlannedTask task, JavaFileSummary summary) {
        Set<String> taskTerms = terms(task.getTitle() + " " + task.getType() + " " + task.getTargetFiles());
        Set<String> sourceTerms = terms(summary.getPrimaryType() + " " + summary.getPath() + " " + summary.getDependencies());
        if (taskTerms.isEmpty() || sourceTerms.isEmpty()) {
            return 0.0d;
        }
        int hits = 0;
        for (String term : taskTerms) {
            if (sourceTerms.contains(term)) {
                hits++;
            }
        }
        return Math.min(1.0d, hits / (double) taskTerms.size());
    }

    /**
     * Scores direct graph proximity to target file or symbol hints.
     */
    private double dependencyProximity(PlannedTask task, JavaFileSummary summary, ProjectGraph graph) {
        for (String target : task.getTargetFiles()) {
            if (target.contains(summary.getPrimaryType()) || target.contains(summary.getPath())) {
                return 1.0d;
            }
            if (graph.isAdjacent(summary.getPrimaryType(), target) || graph.isAdjacent(summary.getPath(), target)) {
                return 0.75d;
            }
        }
        return 0.0d;
    }

    /**
     * Scores relation to requirement/task identifiers.
     */
    private double requirementRelation(PlannedTask task, JavaFileSummary summary) {
        String haystack = (summary.getPath() + " " + summary.getPrimaryType()).toLowerCase(Locale.ROOT);
        String requirementNumber = task.getRequirementId().replaceAll("[^0-9]", "");
        if (requirementNumber.length() > 0 && haystack.contains(requirementNumber)) {
            return 1.0d;
        }
        return haystack.contains(task.getType().toLowerCase(Locale.ROOT)) ? 0.5d : 0.0d;
    }

    /**
     * Placeholder for future VCS recency; MVP favors target files.
     */
    private double recentModification(PlannedTask task, JavaFileSummary summary) {
        for (String target : task.getTargetFiles()) {
            if (target.contains(summary.getPrimaryType()) || target.contains(summary.getPath())) {
                return 1.0d;
            }
        }
        return 0.0d;
    }

    /**
     * Splits text into lowercase alphanumeric terms.
     */
    private static Set<String> terms(String text) {
        Set<String> values = new HashSet<String>();
        for (String raw : text.toLowerCase(Locale.ROOT).split("[^a-z0-9]+")) {
            if (raw.length() > 1) {
                values.add(raw);
            }
        }
        return values;
    }
}

