package es.uco.ecoagent.core.orchestration;

import es.uco.ecoagent.core.agents.AgentRole;
import es.uco.ecoagent.core.blackboard.Blackboard;
import es.uco.ecoagent.core.blackboard.BlackboardArtifact;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Coordinates agents through explicit states and blackboard artifacts.
 */
public final class MultiAgentOrchestrator {
    private final Blackboard blackboard;
    private final List<OrchestrationTransition> history = new ArrayList<OrchestrationTransition>();
    private OrchestrationState state = OrchestrationState.NEW;
    private static final Map<OrchestrationState, List<OrchestrationState>> ALLOWED = allowedTransitions();

    /**
     * Creates an orchestrator over a file-backed blackboard.
     */
    public MultiAgentOrchestrator(Blackboard blackboard) {
        this.blackboard = blackboard;
        this.blackboard.initialize();
    }

    /**
     * Records an agent output artifact and advances the deterministic workflow.
     */
    public OrchestrationTransition acceptArtifact(AgentRole role, BlackboardArtifact artifact, String content) {
        OrchestrationState next = nextStateFor(role);
        requireAllowed(next);
        blackboard.write(artifact, content);
        OrchestrationTransition transition = new OrchestrationTransition(state, next, role, artifact.getFileName(), Instant.now());
        history.add(transition);
        state = next;
        return transition;
    }

    /**
     * Records deterministic planner output and moves to TASKS_READY.
     */
    public OrchestrationTransition markTasksReady(String taskStatusJson) {
        OrchestrationState next = OrchestrationState.TASKS_READY;
        requireAllowed(next);
        blackboard.write(BlackboardArtifact.TASK_STATUS, taskStatusJson);
        OrchestrationTransition transition = new OrchestrationTransition(state, next, AgentRole.REVIEWER, BlackboardArtifact.TASK_STATUS.getFileName(), Instant.now());
        history.add(transition);
        state = next;
        return transition;
    }

    /**
     * Advances verification states that are produced by deterministic tools.
     */
    public OrchestrationTransition advanceVerification(boolean success, String artifact) {
        OrchestrationState next = success ? OrchestrationState.DONE : OrchestrationState.NEEDS_REVIEW;
        requireAllowed(next);
        OrchestrationTransition transition = new OrchestrationTransition(state, next, AgentRole.REVIEWER, artifact, Instant.now());
        history.add(transition);
        state = next;
        return transition;
    }

    /**
     * Returns the current orchestration state.
     */
    public OrchestrationState getState() {
        return state;
    }

    /**
     * Returns immutable transition history.
     */
    public List<OrchestrationTransition> getHistory() {
        return Collections.unmodifiableList(history);
    }

    /**
     * Maps an agent role to the state its output unlocks.
     */
    private static OrchestrationState nextStateFor(AgentRole role) {
        if (role == AgentRole.REQUIREMENTS) {
            return OrchestrationState.REQUIREMENTS_READY;
        }
        if (role == AgentRole.ARCHITECT) {
            return OrchestrationState.ARCHITECTURE_READY;
        }
        if (role == AgentRole.BACKEND || role == AgentRole.FRONTEND || role == AgentRole.TEST || role == AgentRole.REPAIR) {
            return OrchestrationState.PATCH_READY;
        }
        if (role == AgentRole.REVIEWER) {
            return OrchestrationState.VERIFYING;
        }
        throw new IllegalArgumentException("Unsupported role transition: " + role);
    }

    /**
     * Fails if the requested state is not valid from the current state.
     */
    private void requireAllowed(OrchestrationState next) {
        List<OrchestrationState> nextStates = ALLOWED.get(state);
        if (nextStates == null || !nextStates.contains(next)) {
            throw new IllegalStateException("Invalid orchestration transition from " + state + " to " + next);
        }
    }

    /**
     * Defines the explicit multi-agent workflow.
     */
    private static Map<OrchestrationState, List<OrchestrationState>> allowedTransitions() {
        Map<OrchestrationState, List<OrchestrationState>> transitions = new EnumMap<OrchestrationState, List<OrchestrationState>>(OrchestrationState.class);
        transitions.put(OrchestrationState.NEW, Collections.singletonList(OrchestrationState.REQUIREMENTS_READY));
        transitions.put(OrchestrationState.REQUIREMENTS_READY, Collections.singletonList(OrchestrationState.ARCHITECTURE_READY));
        transitions.put(OrchestrationState.ARCHITECTURE_READY, asList(OrchestrationState.TASKS_READY, OrchestrationState.PATCH_READY));
        transitions.put(OrchestrationState.TASKS_READY, Collections.singletonList(OrchestrationState.PATCH_READY));
        transitions.put(OrchestrationState.PATCH_READY, Collections.singletonList(OrchestrationState.VERIFYING));
        transitions.put(OrchestrationState.VERIFYING, asList(OrchestrationState.DONE, OrchestrationState.NEEDS_REVIEW, OrchestrationState.PATCH_READY));
        transitions.put(OrchestrationState.DONE, Collections.<OrchestrationState>emptyList());
        transitions.put(OrchestrationState.NEEDS_REVIEW, Collections.<OrchestrationState>emptyList());
        return transitions;
    }

    /**
     * Creates an immutable two-value list.
     */
    private static List<OrchestrationState> asList(OrchestrationState first, OrchestrationState second) {
        List<OrchestrationState> values = new ArrayList<OrchestrationState>();
        values.add(first);
        values.add(second);
        return Collections.unmodifiableList(values);
    }

    /**
     * Creates an immutable three-value list.
     */
    private static List<OrchestrationState> asList(OrchestrationState first, OrchestrationState second, OrchestrationState third) {
        List<OrchestrationState> values = new ArrayList<OrchestrationState>();
        values.add(first);
        values.add(second);
        values.add(third);
        return Collections.unmodifiableList(values);
    }
}

