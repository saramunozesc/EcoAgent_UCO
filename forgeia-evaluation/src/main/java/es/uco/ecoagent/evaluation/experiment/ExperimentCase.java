package es.uco.ecoagent.evaluation.experiment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Defines one benchmark requirement executed with multiple context strategies.
 */
public final class ExperimentCase {
    private final String applicationId;
    private final String requirementId;
    private final String requirementText;
    private final List<String> acceptanceCriteria;

    /**
     * Creates an immutable experiment case.
     */
    public ExperimentCase(String applicationId, String requirementId, String requirementText,
            List<String> acceptanceCriteria) {
        this.applicationId = applicationId;
        this.requirementId = requirementId;
        this.requirementText = requirementText;
        this.acceptanceCriteria = Collections.unmodifiableList(new ArrayList<String>(acceptanceCriteria));
    }

    /**
     * Returns the benchmark application identifier.
     */
    public String getApplicationId() {
        return applicationId;
    }

    /**
     * Returns the requirement identifier.
     */
    public String getRequirementId() {
        return requirementId;
    }

    /**
     * Returns the requirement text.
     */
    public String getRequirementText() {
        return requirementText;
    }

    /**
     * Returns criteria used to evaluate quality.
     */
    public List<String> getAcceptanceCriteria() {
        return acceptanceCriteria;
    }
}
