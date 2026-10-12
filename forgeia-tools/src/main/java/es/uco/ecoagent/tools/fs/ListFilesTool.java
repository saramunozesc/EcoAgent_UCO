package es.uco.ecoagent.tools.fs;

import es.uco.ecoagent.tools.api.Tool;
import es.uco.ecoagent.tools.api.ToolException;
import es.uco.ecoagent.tools.api.ToolExecutionContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Lists files inside the authorized workspace.
 */
public final class ListFilesTool implements Tool<ListFilesInput, ListFilesResult> {
    /**
     * Returns the stable tool name.
     */
    public String name() {
        return "listFiles";
    }

    /**
     * Walks the requested path and returns matching regular files.
     */
    public ListFilesResult execute(ToolExecutionContext context, ListFilesInput input) {
        Path root = context.getPathPolicy().resolveInsideWorkspace(input.getPath());
        List<String> files = new ArrayList<String>();
        if (!Files.exists(root)) {
            return new ListFilesResult(files);
        }
        try {
            Files.walk(root).filter(Files::isRegularFile).forEach(path -> {
                String relative = context.getWorkspace().relativize(path).toString().replace("\\", "/");
                if (input.getContains() == null || relative.contains(input.getContains())) {
                    files.add(relative);
                }
            });
        } catch (IOException exception) {
            throw new ToolException("Cannot list files below: " + input.getPath(), exception);
        }
        Collections.sort(files);
        return new ListFilesResult(files);
    }
}

