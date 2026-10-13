package es.uco.ecoagent.core.domain;

import org.junit.Test;

import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Verifies internal data model artifacts from section 16.
 */
public final class DomainArtifactsTest {
    /**
     * Keeps Project stack metadata available for summaries and UI.
     */
    @Test
    public void projectIncludesStack() {
        Project project = new Project("p1", "MathKids", Paths.get("workspace"), "Java/Spring",
                ProjectStatus.ACTIVE, Instant.parse("2026-10-13T12:00:00Z"));

        assertEquals("Java/Spring", project.getStack());
        assertEquals(ProjectStatus.ACTIVE, project.getStatus());
    }

    /**
     * Stores patch diff, files, hash and applied flag.
     */
    @Test
    public void patchCapturesTraceableFields() {
        Patch patch = new Patch("TASK-1", "diff --git", Arrays.asList("A.java"), "abc123", false);

        assertEquals("TASK-1", patch.getTaskId());
        assertEquals("abc123", patch.getHash());
        assertEquals("A.java", patch.getFiles().get(0));
        assertFalse(patch.isApplied());
    }

    /**
     * Stores build, tests, errors and duration for validation.
     */
    @Test
    public void validationRunCapturesBuildAndTests() {
        ValidationRun validation = new ValidationRun("PASS", 18, 0, Arrays.asList("none"), 1200L);

        assertEquals("PASS", validation.getBuildStatus());
        assertEquals(18, validation.getTestsPassed());
        assertEquals(0, validation.getTestsFailed());
        assertEquals(1200L, validation.getDurationMillis());
    }
}
