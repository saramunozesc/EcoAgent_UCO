package es.uco.ecoagent.tools.api;

import java.util.List;

/**
 * Extracts affected files from a structured tool result.
 */
public interface AffectedFilesExtractor<O> {
    /**
     * Returns workspace-relative files affected by a tool result.
     */
    List<String> extract(O output);
}
