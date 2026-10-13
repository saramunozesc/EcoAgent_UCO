package es.uco.ecoagent.core.agents;

/**
 * Enumerates the specialized agents used by the multi-agent phase.
 */
public enum AgentRole {
    REQUIREMENTS("Interpretar producto/requisito", "RequirementSpec"),
    ARCHITECT("Decisiones estructurales y contratos", "ArchitectureDecision"),
    BACKEND("Cambios Java/Spring", "PatchProposal"),
    FRONTEND("Cambios React/TypeScript", "PatchProposal"),
    TEST("Generar pruebas dirigidas", "TestPatch"),
    REVIEWER("Buscar incumplimientos/riesgos", "ReviewReport"),
    REPAIR("Corregir fallos observados", "RepairPatch");

    private final String responsibility;
    private final String outputArtifact;

    /**
     * Stores the role responsibility and primary output artifact.
     */
    AgentRole(String responsibility, String outputArtifact) {
        this.responsibility = responsibility;
        this.outputArtifact = outputArtifact;
    }

    /**
     * Returns the role's main responsibility.
     */
    public String getResponsibility() {
        return responsibility;
    }

    /**
     * Returns the main artifact produced by the role.
     */
    public String getOutputArtifact() {
        return outputArtifact;
    }
}

