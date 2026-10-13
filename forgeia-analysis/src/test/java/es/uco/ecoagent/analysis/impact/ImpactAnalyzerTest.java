package es.uco.ecoagent.analysis.impact;

import es.uco.ecoagent.analysis.javaast.JavaFileSummary;
import es.uco.ecoagent.analysis.project.ProjectSummary;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Verifies pre-change impact analysis for new requirements.
 */
public final class ImpactAnalyzerTest {
    /**
     * Ranks matching project components before any patch is applied.
     */
    @Test
    public void ranksAffectedComponents() {
        JavaFileSummary controller = new JavaFileSummary("src/UserController.java", "demo", "UserController",
                Collections.<String>emptyList(), Arrays.asList("UserService"),
                Arrays.asList("RestController"), Collections.emptyList());
        JavaFileSummary entity = new JavaFileSummary("src/User.java", "demo", "User",
                Collections.<String>emptyList(), Collections.<String>emptyList(),
                Arrays.asList("Entity"), Collections.emptyList());
        ProjectSummary summary = new ProjectSummary("Demo", 2, 2, Arrays.asList("."),
                Arrays.asList("UserController"), Arrays.asList("User"), Arrays.asList("UserService"),
                Arrays.asList(controller, entity));

        ImpactAnalysis analysis = new ImpactAnalyzer().analyze(summary, "nuevo endpoint REST de usuario");

        assertTrue(analysis.isRequiresApproval());
        assertEquals("UserController", analysis.getAffectedComponents().get(0).getSymbol());
        assertTrue(analysis.getAffectedComponents().get(0).getScore() > 0.0d);
    }
}
