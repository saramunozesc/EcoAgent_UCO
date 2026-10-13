package es.uco.ecoagent.analysis.impact;

import es.uco.ecoagent.analysis.javaast.JavaFileSummary;
import es.uco.ecoagent.analysis.project.ProjectSummary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Ranks imported project components affected by a new requirement.
 */
public final class ImpactAnalyzer {
    /**
     * Produces a deterministic pre-change impact report.
     */
    public ImpactAnalysis analyze(ProjectSummary summary, String requirementText) {
        String normalized = normalize(requirementText);
        List<AffectedComponent> affected = new ArrayList<AffectedComponent>();
        for (JavaFileSummary javaSummary : summary.getJavaSummaries()) {
            double score = score(javaSummary, normalized);
            if (score > 0.0d) {
                affected.add(new AffectedComponent(javaSummary.getPath(), javaSummary.getPrimaryType(), score,
                        reason(javaSummary, normalized)));
            }
        }
        Collections.sort(affected, Comparator.comparingDouble(AffectedComponent::getScore).reversed()
                .thenComparing(AffectedComponent::getPath));
        return new ImpactAnalysis(requirementText, affected, true);
    }

    /**
     * Scores a Java file using names, paths and known annotations.
     */
    private static double score(JavaFileSummary summary, String requirementText) {
        double score = 0.0d;
        String haystack = normalize(summary.getPath() + " " + summary.getPrimaryType() + " " + summary.getDependencies());
        for (String token : requirementText.split(" ")) {
            if (token.length() > 2 && haystack.contains(token)) {
                score += 1.0d;
            }
        }
        if (summary.getAnnotations().contains("RestController") || summary.getAnnotations().contains("Controller")) {
            score += containsAny(requirementText, "endpoint", "api", "rest") ? 0.75d : 0.0d;
        }
        if (summary.getAnnotations().contains("Entity")) {
            score += containsAny(requirementText, "entity", "entidad", "dato", "bd") ? 0.75d : 0.0d;
        }
        return score;
    }

    /**
     * Builds a compact human-readable reason.
     */
    private static String reason(JavaFileSummary summary, String requirementText) {
        if (summary.getAnnotations().contains("RestController") || summary.getAnnotations().contains("Controller")) {
            return "controller candidate for API change";
        }
        if (summary.getAnnotations().contains("Entity")) {
            return "entity candidate for data change";
        }
        return "name or dependency matches requirement text";
    }

    /**
     * Returns whether normalized text contains any candidate token.
     */
    private static boolean containsAny(String text, String... tokens) {
        for (String token : tokens) {
            if (text.contains(token)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Normalizes text for deterministic keyword matching.
     */
    private static String normalize(String value) {
        return value.toLowerCase().replaceAll("[^a-z0-9]+", " ").trim();
    }
}
