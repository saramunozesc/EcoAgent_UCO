package es.uco.ecoagent.tools.search;

import java.util.Collections;
import java.util.List;

/**
 * Structured result containing all text search matches.
 */
public final class SearchTextResult {
    private final List<SearchMatch> matches;

    /**
     * Stores matches found by searchText.
     */
    public SearchTextResult(List<SearchMatch> matches) {
        this.matches = Collections.unmodifiableList(matches);
    }

    /**
     * Returns the ordered search matches.
     */
    public List<SearchMatch> getMatches() {
        return matches;
    }
}

