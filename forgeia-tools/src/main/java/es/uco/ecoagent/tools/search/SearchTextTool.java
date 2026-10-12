package es.uco.ecoagent.tools.search;

import es.uco.ecoagent.tools.api.Tool;
import es.uco.ecoagent.tools.api.ToolException;
import es.uco.ecoagent.tools.api.ToolExecutionContext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Searches plain text across regular files in the workspace.
 */
public final class SearchTextTool implements Tool<SearchTextInput, SearchTextResult> {
    /**
     * Returns the stable tool name.
     */
    public String name() {
        return "searchText";
    }

    /**
     * Finds query occurrences and returns file, line number and line text.
     */
    public SearchTextResult execute(ToolExecutionContext context, SearchTextInput input) {
        List<SearchMatch> matches = new ArrayList<SearchMatch>();
        try {
            Files.walk(context.getWorkspace()).filter(Files::isRegularFile).forEach(path -> searchFile(context, input, path, matches));
            return new SearchTextResult(matches);
        } catch (IOException exception) {
            throw new ToolException("Cannot search workspace", exception);
        }
    }

    /**
     * Adds matches for one file while skipping unreadable binary-like files.
     */
    private void searchFile(ToolExecutionContext context, SearchTextInput input, Path path, List<SearchMatch> matches) {
        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (int index = 0; index < lines.size(); index++) {
                if (lines.get(index).contains(input.getQuery())) {
                    String relative = context.getWorkspace().relativize(path).toString().replace("\\", "/");
                    matches.add(new SearchMatch(relative, index + 1, lines.get(index)));
                }
            }
        } catch (IOException ignored) {
            // Files that cannot be read as UTF-8 text are ignored by this deterministic search.
        }
    }
}

