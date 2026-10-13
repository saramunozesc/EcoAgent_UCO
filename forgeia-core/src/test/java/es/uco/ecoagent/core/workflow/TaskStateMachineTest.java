package es.uco.ecoagent.core.workflow;

import es.uco.ecoagent.core.domain.TaskStatus;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Verifies task workflow transitions and audit history.
 */
public class TaskStateMachineTest {
    /**
     * Ensures the happy path reaches DONE and records each transition.
     */
    @Test
    public void recordsValidTransitions() {
        TaskStateMachine machine = new TaskStateMachine("TASK-004-1", TaskStatus.PENDING);

        machine.transitionTo(TaskStatus.PLANNED, "planned");
        machine.transitionTo(TaskStatus.READY, "dependencies met");
        machine.transitionTo(TaskStatus.RUNNING, "executor started");
        machine.transitionTo(TaskStatus.VERIFYING, "patch proposed");
        machine.transitionTo(TaskStatus.DONE, "tests passed");

        assertEquals(TaskStatus.DONE, machine.getStatus());
        assertEquals(5, machine.getHistory().size());
        assertEquals(TaskStatus.VERIFYING, machine.getHistory().get(4).getFromStatus());
    }

    /**
     * Ensures invalid transitions fail before corrupting state.
     */
    @Test(expected = IllegalStateException.class)
    public void rejectsInvalidTransitions() {
        TaskStateMachine machine = new TaskStateMachine("TASK-004-1", TaskStatus.PENDING);
        machine.transitionTo(TaskStatus.RUNNING, "skip planning");
    }

    /**
     * Ensures unresolved failures can be parked for human review.
     */
    @Test
    public void canMoveFailedTaskToNeedsReview() {
        TaskStateMachine machine = new TaskStateMachine("TASK-004-1", TaskStatus.FAILED);

        machine.transitionTo(TaskStatus.NEEDS_REVIEW, "repair attempts exhausted");

        assertEquals(TaskStatus.NEEDS_REVIEW, machine.getStatus());
        assertEquals(1, machine.getHistory().size());
    }
}

