package es.uco.ecoagent.analysis.impact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Captures the pre-change impact analysis shown for user approval.
 */
public final class ImpactAnalysis {
    private final String requirementText;
    private final List<AffectedComponent> affectedComponents;
    private final boolean requiresApproval;

    /**
     * Creates an immutable impact analysis.
     */
    public ImpactAnalysis(String requirementText, List<AffectedComponent> affectedComponents, boolean requiresApproval) {
        this.requirementText = requirementText;
        this.affectedComponents = Collections.unmodifiableList(new ArrayList<AffectedComponent>(affectedComponents));
        this.requiresApproval = requiresApproval;
    }

    /**
     * Returns the raw user improvement request.
     */
    public String getRequirementText() {
        return requirementText;
    }

    /**
     * Returns ranked components likely to change.
     */
    public List<AffectedComponent> getAffectedComponents() {
        return affectedComponents;
    }

    /**
     * Returns whether approval is required before modifying files.
     */
    public boolean isRequiresApproval() {
        return requiresApproval;
    }
}
