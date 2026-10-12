package es.uco.ecoagent.tools.fs;

import es.uco.ecoagent.tools.api.Tool;
import es.uco.ecoagent.tools.api.ToolException;
import es.uco.ecoagent.tools.api.ToolExecutionContext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Reads text files from the authorized workspace.
 */
public final class ReadFileTool implements Tool<ReadFileInput, ReadFileResult> {
    /**
     * Returns the stable tool name.
     */
    public String name() {
        return "readFile";
    }

    /**
     * Reads the full file or the requested inclusive line range.
     */
    public ReadFileResult execute(ToolExecutionContext context, ReadFileInput input) {
        Path file = context.getPathPolicy().resolveInsideWorkspace(input.getPath());
        try {
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            int start = input.getStartLine() == null ? 1 : input.getStartLine().intValue();
            int end = input.getEndLine() == null ? lines.size() : input.getEndLine().intValue();
            if (start < 1 || end < start) {
                throw new ToolException("Invalid line range");
            }
            StringBuilder builder = new StringBuilder();
            for (int index = start; index <= end && index <= lines.size(); index++) {
                if (builder.length() > 0) {
                    builder.append(System.lineSeparator());
                }
                builder.append(lines.get(index - 1));
            }
            return new ReadFileResult(builder.toString());
        } catch (IOException exception) {
            throw new ToolException("Cannot read file: " + input.getPath(), exception);
        }
    }
}

