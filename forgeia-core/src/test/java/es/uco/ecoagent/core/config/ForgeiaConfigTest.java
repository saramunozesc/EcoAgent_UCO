package es.uco.ecoagent.core.config;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;

/**
 * Verifies that external ForgeIA configuration is parsed correctly.
 */
public class ForgeiaConfigTest {
    /**
     * Ensures custom properties override the default configuration values.
     */
    @Test
    public void loadsExternalConfigValues() throws Exception {
        Path configFile = Files.createTempFile("forgeia", ".properties");
        Files.write(configFile, (
                "llm.provider=test-provider\n"
                        + "llm.model=test-model\n"
                        + "workspace.root=tmp-workspace\n"
                        + "logs.root=tmp-logs\n"
                        + "limits.maxContextTokens=100\n"
                        + "limits.maxOutputTokens=50\n"
                        + "limits.maxLlmCalls=2\n"
                        + "limits.maxRepairAttempts=1\n").getBytes("UTF-8"));

        ForgeiaConfig config = ForgeiaConfig.load(configFile);

        assertEquals("test-provider", config.getProvider());
        assertEquals("test-model", config.getModel());
        assertEquals(100, config.getMaxContextTokens());
        assertEquals(50, config.getMaxOutputTokens());
        assertEquals(2, config.getMaxLlmCalls());
        assertEquals(1, config.getMaxRepairAttempts());
    }
}

