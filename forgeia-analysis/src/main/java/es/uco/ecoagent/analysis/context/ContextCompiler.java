package es.uco.ecoagent.analysis.context;

import es.uco.ecoagent.analysis.graph.DependencyGraphBuilder;
import es.uco.ecoagent.analysis.graph.ProjectGraph;
import es.uco.ecoagent.analysis.javaast.JavaAstIndexer;
import es.uco.ecoagent.analysis.javaast.JavaFileSummary;
import es.uco.ecoagent.core.planning.PlannedTask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Builds a small ContextSlice from task, AST summaries, graph and budget.
 */
public final class ContextCompiler {
    private final JavaAstIndexer indexer;
    private final DependencyGraphBuilder graphBuilder;
    private final ContextRanker ranker;
    private final TokenEstimator tokenEstimator;

    /**
     * Creates a compiler with deterministic analysis components.
     */
    public ContextCompiler() {
        this(new JavaAstIndexer(), new DependencyGraphBuilder(), new ContextRanker(), new TokenEstimator());
    }

    /**
     * Creates a compiler with injected components for tests.
     */
    public ContextCompiler(JavaAstIndexer indexer, DependencyGraphBuilder graphBuilder, ContextRanker ranker, TokenEstimator tokenEstimator) {
        this.indexer = indexer;
        this.graphBuilder = graphBuilder;
        this.ranker = ranker;
        this.tokenEstimator = tokenEstimator;
    }

    /**
     * Compiles the best context items without exceeding maxContextTokens.
     */
    public ContextSlice compile(Path workspaceRoot, PlannedTask task) {
        List<JavaFileSummary> summaries = indexer.index(workspaceRoot);
        ProjectGraph graph = graphBuilder.build(summaries);
        List<ContextCandidate> candidates = rank(workspaceRoot, task, summaries, graph);
        List<ContextItem> selected = new ArrayList<ContextItem>();
        int budget = task.getBudget().getMaxContextTokens();
        int tokens = 0;
        for (ContextCandidate candidate : candidates) {
            ContextItem item = chooseLevel(candidate, budget - tokens);
            if (item != null && tokens + item.getTokenEstimate() <= budget) {
                selected.add(item);
                tokens += item.getTokenEstimate();
            }
        }
        return new ContextSlice(task.getId(), selected, tokens, "weighted-ast-graph-budget");
    }

    /**
     * Ranks all Java summaries by weighted relevance.
     */
    private List<ContextCandidate> rank(Path workspaceRoot, PlannedTask task, List<JavaFileSummary> summaries, ProjectGraph graph) {
        List<ContextCandidate> candidates = new ArrayList<ContextCandidate>();
        for (JavaFileSummary summary : summaries) {
            candidates.add(new ContextCandidate(summary, readFile(workspaceRoot.resolve(summary.getPath())), ranker.score(task, summary, graph)));
        }
        Collections.sort(candidates, new Comparator<ContextCandidate>() {
            public int compare(ContextCandidate left, ContextCandidate right) {
                int byScore = Double.compare(right.getScore(), left.getScore());
                if (byScore != 0) {
                    return byScore;
                }
                return left.getSummary().getPath().compareTo(right.getSummary().getPath());
            }
        });
        return candidates;
    }

    /**
     * Chooses L3, then L2, then L1/L0 depending on remaining budget.
     */
    private ContextItem chooseLevel(ContextCandidate candidate, int remainingBudget) {
        String full = candidate.getFullText();
        int fullTokens = tokenEstimator.estimate(full);
        if (fullTokens <= remainingBudget) {
            return new ContextItem(candidate.getSummary().getPath(), ContextLevel.L3_FULL_FILE, full, fullTokens, candidate.getScore());
        }

        String snippet = snippet(full);
        int snippetTokens = tokenEstimator.estimate(snippet);
        if (snippetTokens <= remainingBudget) {
            return new ContextItem(candidate.getSummary().getPath(), ContextLevel.L2_SNIPPET, snippet, snippetTokens, candidate.getScore());
        }

        String summary = candidate.getSummary().toSummaryText();
        int summaryTokens = tokenEstimator.estimate(summary);
        if (summaryTokens <= remainingBudget) {
            return new ContextItem(candidate.getSummary().getPath(), ContextLevel.L1_SUMMARY, summary, summaryTokens, candidate.getScore());
        }

        String metadata = candidate.getSummary().getPrimaryType() + " " + candidate.getSummary().getPath();
        int metadataTokens = tokenEstimator.estimate(metadata);
        if (metadataTokens <= remainingBudget) {
            return new ContextItem(candidate.getSummary().getPath(), ContextLevel.L0_METADATA, metadata, metadataTokens, candidate.getScore());
        }
        return null;
    }

    /**
     * Returns a bounded snippet from the beginning of a file.
     */
    private static String snippet(String full) {
        return full.length() <= 800 ? full : full.substring(0, 800);
    }

    /**
     * Reads source text using UTF-8.
     */
    private static String readFile(Path path) {
        try {
            return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot read source file: " + path, exception);
        }
    }
}

