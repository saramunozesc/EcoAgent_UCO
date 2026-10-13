package es.uco.ecoagent.core.planning;

import es.uco.ecoagent.core.domain.Requirement;
import es.uco.ecoagent.core.domain.RequirementPriority;
import es.uco.ecoagent.core.domain.RequirementStatus;
import es.uco.ecoagent.core.domain.TaskBudget;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Verifies deterministic task planning for requirements.
 */
public class RequirementPlannerTest {
    /**
     * Ensures a scoring requirement becomes entity, repository, service, endpoint and test tasks.
     */
    @Test
    public void plansScoringRequirementIntoSmallTasks() {
        Requirement requirement = new Requirement(
                "REQ-004",
                "Sistema de puntuacion",
                "Gestiona puntuaciones",
                Collections.singletonList("Se puede guardar puntuacion"),
                RequirementPriority.MUST,
                RequirementStatus.APPROVED);
        RequirementPlanner planner = new RequirementPlanner(new TaskBudget(8000, 2500, 4, 2));

        RequirementPlan plan = planner.plan(requirement);

        assertEquals("REQ-004", plan.getRequirementId());
        assertEquals(5, plan.getTasks().size());
        assertEquals("TASK-004-1", plan.getTasks().get(0).getId());
        assertEquals("Crear entidad Score", plan.getTasks().get(0).getTitle());
        assertEquals("Implementar ScoreService", plan.getTasks().get(2).getTitle());
        assertTrue(plan.getTasks().get(4).getTargetFiles().get(0).contains("Score"));
        assertEquals("TASK-004-4", plan.getTasks().get(4).getDependencies().get(0));
    }
}

