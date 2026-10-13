package es.uco.ecoagent.core.workflow;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Verifies repair attempts are bounded.
 */
public class RepairPolicyTest {
    /**
     * Ensures attempts stop once the configured limit is reached.
     */
    @Test
    public void limitsRepairAttempts() {
        RepairPolicy policy = new RepairPolicy(2);

        assertTrue(policy.canAttempt(0));
        assertTrue(policy.canAttempt(1));
        assertFalse(policy.canAttempt(2));
    }
}

