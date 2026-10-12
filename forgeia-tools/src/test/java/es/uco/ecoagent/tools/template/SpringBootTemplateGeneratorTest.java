package es.uco.ecoagent.tools.template;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Verifies the deterministic Spring Boot template generator.
 */
public class SpringBootTemplateGeneratorTest {
    /**
     * Ensures the template writes the expected build, endpoint and test files.
     */
    @Test
    public void generatesKnownSpringBootStructure() throws Exception {
        Path target = Files.createTempDirectory("forgeia-template");

        TemplateResult result = new SpringBootTemplateGenerator().generate(target, "es.uco.demo", "task-app");

        assertEquals(8, result.getFileCount());
        assertTrue(Files.isRegularFile(target.resolve("pom.xml")));
        assertTrue(Files.isRegularFile(target.resolve("src/main/resources/application.yml")));
        assertTrue(Files.isRegularFile(target.resolve("src/main/java/es/uco/demo/taskapp/health/HealthController.java")));
        assertTrue(Files.isRegularFile(target.resolve("src/test/java/es/uco/demo/taskapp/health/HealthControllerTest.java")));
    }
}

