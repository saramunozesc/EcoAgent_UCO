package es.uco.ecoagent.tools.search;

/**
 * Input for searching text inside workspace files.
 */
public final class SearchTextInput {
    private final String query;

    /**
     * Creates a text search request.
     */
    public SearchTextInput(String query) {
        this.query = query;
    }

    /**
     * Returns the plain text query to find.
     */
    public String getQuery() {
        return query;
    }
}

