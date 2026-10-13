package es.uco.ecoagent.evaluation.benchmark;

import es.uco.ecoagent.evaluation.experiment.ExperimentCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Creates the small controlled benchmark recommended for the TFG.
 */
public final class BenchmarkDatasetFactory {
    /**
     * Builds a starter dataset with 5 apps and 10 requirements.
     */
    public BenchmarkDataset starterDataset() {
        List<BenchmarkApplication> applications = Arrays.asList(
                new BenchmarkApplication("mathkids", "MathKids", "templates/mathkids"),
                new BenchmarkApplication("library", "Library API", "templates/library"),
                new BenchmarkApplication("fitness", "Fitness Tracker", "templates/fitness"),
                new BenchmarkApplication("recipes", "Recipe Book", "templates/recipes"),
                new BenchmarkApplication("tickets", "Ticket Desk", "templates/tickets"));

        List<ExperimentCase> cases = new ArrayList<ExperimentCase>();
        cases.add(new ExperimentCase("mathkids", "REQ-001", "Anadir logros por partidas perfectas",
                Arrays.asList("Se concede logro tras cinco partidas perfectas", "No se duplica el logro")));
        cases.add(new ExperimentCase("mathkids", "REQ-002", "Mostrar progreso semanal",
                Arrays.asList("Devuelve puntos por dia", "Incluye total semanal")));
        cases.add(new ExperimentCase("library", "REQ-003", "Reservar libros prestados",
                Arrays.asList("Reserva solo libros no disponibles", "Lista reservas pendientes")));
        cases.add(new ExperimentCase("library", "REQ-004", "Avisar devoluciones vencidas",
                Arrays.asList("Detecta prestamos vencidos", "Expone endpoint de avisos")));
        cases.add(new ExperimentCase("fitness", "REQ-005", "Calcular racha de entrenamientos",
                Arrays.asList("Cuenta dias consecutivos", "Reinicia racha si falta un dia")));
        cases.add(new ExperimentCase("fitness", "REQ-006", "Registrar objetivos personales",
                Arrays.asList("Crea objetivo por usuario", "Valida fecha objetivo futura")));
        cases.add(new ExperimentCase("recipes", "REQ-007", "Filtrar recetas por alergeno",
                Arrays.asList("Excluye recetas con alergeno", "Permite filtros combinados")));
        cases.add(new ExperimentCase("recipes", "REQ-008", "Escalar ingredientes por raciones",
                Arrays.asList("Multiplica cantidades", "Mantiene unidades originales")));
        cases.add(new ExperimentCase("tickets", "REQ-009", "Asignar prioridad por SLA",
                Arrays.asList("Calcula prioridad por fecha limite", "Actualiza tickets existentes")));
        cases.add(new ExperimentCase("tickets", "REQ-010", "Resumen de tickets por agente",
                Arrays.asList("Agrupa por agente", "Incluye abiertos y cerrados")));
        return new BenchmarkDataset(applications, cases);
    }
}
