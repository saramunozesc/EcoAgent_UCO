package es.uco.ecoagent.core.blackboard;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Verifies file-backed blackboard artifact storage.
 */
public class BlackboardTest {
    /**
     * Ensures structured artifacts are written and read from blackboard/.
     */
    @Test
    public void writesAndReadsArtifacts() throws Exception {
        Path workspace = Files.createTempDirectory("blackboard");
        Blackboard blackboard = new Blackboard(workspace);

        Path artifact = blackboard.write(BlackboardArtifact.REQUIREMENTS, "{\"projectName\":\"Demo\"}");
        blackboard.appendMetric("{\"tokens\":10}");

        assertTrue(artifact.toString().replace("\\", "/").endsWith("blackboard/requirements.json"));
        assertEquals("{\"projectName\":\"Demo\"}", blackboard.read(BlackboardArtifact.REQUIREMENTS));
        assertTrue(Files.isRegularFile(blackboard.resolve(BlackboardArtifact.METRICS)));
    }
}

