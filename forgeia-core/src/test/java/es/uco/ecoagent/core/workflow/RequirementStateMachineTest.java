package es.uco.ecoagent.core.workflow;

import es.uco.ecoagent.core.domain.RequirementStatus;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Verifies the requirement lifecycle from section 16.2.
 */
public final class RequirementStateMachineTest {
    /**
     * Allows the normal DRAFT to DONE requirement path.
     */
    @Test
    public void followsHappyPath() {
        RequirementStateMachine machine = new RequirementStateMachine("REQ-014", RequirementStatus.DRAFT);

        machine.transitionTo(RequirementStatus.APPROVED, "user approved");
        machine.transitionTo(RequirementStatus.PLANNED, "planner created tasks");
        machine.transitionTo(RequirementStatus.IN_PROGRESS, "executor started");
        machine.transitionTo(RequirementStatus.VERIFYING, "patch applied");
        machine.transitionTo(RequirementStatus.DONE, "tests passed");

        assertEquals(RequirementStatus.DONE, machine.getStatus());
        assertEquals(5, machine.getHistory().size());
    }

    /**
     * Rejects transitions that skip approval and planning.
     */
    @Test(expected = IllegalStateException.class)
    public void rejectsInvalidTransition() {
        RequirementStateMachine machine = new RequirementStateMachine("REQ-014", RequirementStatus.DRAFT);

        machine.transitionTo(RequirementStatus.IN_PROGRESS, "skip");
    }

    /**
     * Allows controlled review exits from active work.
     */
    @Test
    public void allowsNeedsReviewFromInProgress() {
        RequirementStateMachine machine = new RequirementStateMachine("REQ-014", RequirementStatus.IN_PROGRESS);

        RequirementTransition transition = machine.transitionTo(RequirementStatus.NEEDS_REVIEW, "manual check");

        assertEquals(RequirementStatus.IN_PROGRESS, transition.getFrom());
        assertEquals(RequirementStatus.NEEDS_REVIEW, transition.getTo());
        assertEquals(RequirementStatus.NEEDS_REVIEW, machine.getStatus());
    }
}
