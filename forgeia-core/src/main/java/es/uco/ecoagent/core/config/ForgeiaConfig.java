package es.uco.ecoagent.core.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Holds external ForgeIA configuration loaded from a properties file.
 */
public final class ForgeiaConfig {
    private final String provider;
    private final String model;
    private final Path workspaceRoot;
    private final Path logsRoot;
    private final int maxContextTokens;
    private final int maxOutputTokens;
    private final int maxLlmCalls;
    private final int maxRepairAttempts;

    /**
     * Builds an immutable configuration object from resolved values.
     */
    private ForgeiaConfig(
            String provider,
            String model,
            Path workspaceRoot,
            Path logsRoot,
            int maxContextTokens,
            int maxOutputTokens,
            int maxLlmCalls,
            int maxRepairAttempts) {
        this.provider = provider;
        this.model = model;
        this.workspaceRoot = workspaceRoot;
        this.logsRoot = logsRoot;
        this.maxContextTokens = maxContextTokens;
        this.maxOutputTokens = maxOutputTokens;
        this.maxLlmCalls = maxLlmCalls;
        this.maxRepairAttempts = maxRepairAttempts;
    }

    /**
     * Loads the default config path or the path supplied with forgeia.config.
     */
    public static ForgeiaConfig loadDefault() {
        Path configPath = Paths.get(System.getProperty("forgeia.config", "config/forgeia.properties"));
        return load(configPath);
    }

    /**
     * Loads configuration values from disk, falling back to safe defaults.
     */
    public static ForgeiaConfig load(Path configPath) {
        Properties properties = new Properties();
        File file = configPath.toFile();
        if (file.exists()) {
            try (FileInputStream input = new FileInputStream(file)) {
                properties.load(input);
            } catch (IOException exception) {
                throw new IllegalStateException("Cannot read config file: " + configPath, exception);
            }
        }

        return new ForgeiaConfig(
                get(properties, "llm.provider", "none"),
                get(properties, "llm.model", "none"),
                Paths.get(get(properties, "workspace.root", "workspace")),
                Paths.get(get(properties, "logs.root", "logs")),
                getInt(properties, "limits.maxContextTokens", 8000),
                getInt(properties, "limits.maxOutputTokens", 2500),
                getInt(properties, "limits.maxLlmCalls", 4),
                getInt(properties, "limits.maxRepairAttempts", 2));
    }

    /**
     * Reads a trimmed string value with a fallback when the key is absent.
     */
    private static String get(Properties properties, String key, String defaultValue) {
        return properties.getProperty(key, defaultValue).trim();
    }

    /**
     * Reads a trimmed integer value with a fallback when the key is absent.
     */
    private static int getInt(Properties properties, String key, int defaultValue) {
        return Integer.parseInt(properties.getProperty(key, String.valueOf(defaultValue)).trim());
    }

    /**
     * Returns the configured LLM provider name.
     */
    public String getProvider() {
        return provider;
    }

    /**
     * Returns the configured LLM model name.
     */
    public String getModel() {
        return model;
    }

    /**
     * Returns the root directory where project sandboxes are created.
     */
    public Path getWorkspaceRoot() {
        return workspaceRoot;
    }

    /**
     * Returns the directory where structured run logs are written.
     */
    public Path getLogsRoot() {
        return logsRoot;
    }

    /**
     * Returns the maximum context tokens allowed per task.
     */
    public int getMaxContextTokens() {
        return maxContextTokens;
    }

    /**
     * Returns the maximum output tokens allowed per LLM call.
     */
    public int getMaxOutputTokens() {
        return maxOutputTokens;
    }

    /**
     * Returns the maximum LLM calls allowed per task.
     */
    public int getMaxLlmCalls() {
        return maxLlmCalls;
    }

    /**
     * Returns the maximum repair attempts allowed per task.
     */
    public int getMaxRepairAttempts() {
        return maxRepairAttempts;
    }
}

