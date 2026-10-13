package es.uco.ecoagent.evaluation.experiment;

/**
 * Names the three recommended context strategies for comparison.
 */
public enum ContextStrategy {
    FULL_CONTEXT("A - Full Context", "enviar todos los archivos que quepan / contexto amplio"),
    BASIC_RETRIEVAL("B - Retrieval basico", "busqueda semantica o textual top-k"),
    FORGEIA_CONTEXT_COMPILER("C - ForgeIA Context Compiler", "AST + dependencias + relevancia + presupuesto");

    private final String label;
    private final String description;

    /**
     * Creates a strategy with its benchmark label.
     */
    ContextStrategy(String label, String description) {
        this.label = label;
        this.description = description;
    }

    /**
     * Returns the report label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns the strategy description.
     */
    public String getDescription() {
        return description;
    }
}
