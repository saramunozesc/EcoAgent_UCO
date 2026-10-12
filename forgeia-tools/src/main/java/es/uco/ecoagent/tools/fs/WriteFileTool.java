package es.uco.ecoagent.tools.fs;

import es.uco.ecoagent.tools.api.Tool;
import es.uco.ecoagent.tools.api.ToolException;
import es.uco.ecoagent.tools.api.ToolExecutionContext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Writes files inside the workspace and returns a content hash.
 */
public final class WriteFileTool implements Tool<WriteFileInput, WriteFileResult> {
    /**
     * Returns the stable tool name.
     */
    public String name() {
        return "writeFile";
    }

    /**
     * Writes UTF-8 content, creating parent directories as needed.
     */
    public WriteFileResult execute(ToolExecutionContext context, WriteFileInput input) {
        Path file = context.getPathPolicy().resolveInsideWorkspace(input.getPath());
        try {
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            byte[] bytes = input.getContent().getBytes(StandardCharsets.UTF_8);
            Files.write(file, bytes);
            return new WriteFileResult(input.getPath(), sha256(bytes));
        } catch (IOException exception) {
            throw new ToolException("Cannot write file: " + input.getPath(), exception);
        }
    }

    /**
     * Calculates a hexadecimal SHA-256 hash for reproducible output.
     */
    private static String sha256(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(bytes);
            StringBuilder builder = new StringBuilder();
            for (byte item : hash) {
                builder.append(String.format("%02x", item & 0xff));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new ToolException("SHA-256 is not available", exception);
        }
    }
}

