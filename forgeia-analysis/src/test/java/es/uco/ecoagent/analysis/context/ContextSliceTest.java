package es.uco.ecoagent.analysis.context;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Verifies section 16 fields on ContextSlice.
 */
public final class ContextSliceTest {
    /**
     * Calculates an aggregate score from selected items.
     */
    @Test
    public void exposesAggregateScore() {
        ContextSlice slice = new ContextSlice("TASK-1", Arrays.asList(
                new ContextItem("A.java", ContextLevel.L1_SUMMARY, "A", 10, 0.8d),
                new ContextItem("B.java", ContextLevel.L2_SNIPPET, "B", 20, 0.4d)), 30, "test");

        assertEquals("TASK-1", slice.getTaskId());
        assertEquals(30, slice.getTokenCount());
        assertEquals("test", slice.getStrategy());
        assertEquals(0.6d, slice.getScore(), 0.0001d);
    }
}
