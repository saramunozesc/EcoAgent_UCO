package es.uco.ecoagent.evaluation.benchmark;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Verifies the small controlled benchmark dataset.
 */
public final class BenchmarkDatasetFactoryTest {
    /**
     * Builds a dataset in the recommended 5-10 apps and 8-12 requirements range.
     */
    @Test
    public void createsStarterBenchmarkDataset() {
        BenchmarkDataset dataset = new BenchmarkDatasetFactory().starterDataset();

        assertEquals(5, dataset.getApplications().size());
        assertEquals(10, dataset.getCases().size());
        assertFalse(dataset.getCases().get(0).getAcceptanceCriteria().isEmpty());
    }
}
