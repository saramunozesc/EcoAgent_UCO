package es.uco.ecoagent.core.planning;

import es.uco.ecoagent.core.domain.Requirement;
import es.uco.ecoagent.core.domain.TaskBudget;
import es.uco.ecoagent.core.domain.TaskStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Splits a requirement into small deterministic implementation tasks.
 */
public final class RequirementPlanner {
    private final TaskBudget defaultBudget;

    /**
     * Creates a planner using one default budget for every generated task.
     */
    public RequirementPlanner(TaskBudget defaultBudget) {
        this.defaultBudget = defaultBudget;
    }

    /**
     * Plans entity, repository, service, endpoint and test tasks.
     */
    public RequirementPlan plan(Requirement requirement) {
        String baseName = inferBaseName(requirement.getTitle());
        String requirementNumber = requirement.getId().replaceAll("[^0-9]", "");
        if (requirementNumber.length() == 0) {
            requirementNumber = "001";
        }

        List<PlannedTask> tasks = new ArrayList<PlannedTask>();
        tasks.add(task(requirement.getId(), requirementNumber, 1, "Crear entidad " + baseName, "ENTITY",
                "src/main/java/**/" + baseName + ".java", Collections.<String>emptyList()));
        tasks.add(task(requirement.getId(), requirementNumber, 2, "Crear repositorio " + baseName, "REPOSITORY",
                "src/main/java/**/" + baseName + "Repository.java", ids(requirementNumber, 1)));
        tasks.add(task(requirement.getId(), requirementNumber, 3, "Implementar " + baseName + "Service", "SERVICE",
                "src/main/java/**/" + baseName + "Service.java", ids(requirementNumber, 2)));
        tasks.add(task(requirement.getId(), requirementNumber, 4, "Crear endpoint REST " + baseName, "CONTROLLER",
                "src/main/java/**/" + baseName + "Controller.java", ids(requirementNumber, 3)));
        tasks.add(task(requirement.getId(), requirementNumber, 5, "Crear tests para " + baseName, "TEST",
                "src/test/java/**/*" + baseName + "*Test.java", ids(requirementNumber, 4)));
        return new RequirementPlan(requirement.getId(), tasks);
    }

    /**
     * Creates one planned task with a deterministic ID and target file.
     */
    private PlannedTask task(String requirementId, String requirementNumber, int index, String title, String type, String targetFile, List<String> dependencies) {
        return new PlannedTask(
                "TASK-" + requirementNumber + "-" + index,
                requirementId,
                title,
                type,
                TaskStatus.PLANNED,
                Collections.singletonList(targetFile),
                dependencies,
                defaultBudget);
    }

    /**
     * Creates dependency IDs for earlier tasks in the same requirement.
     */
    private static List<String> ids(String requirementNumber, int index) {
        return Arrays.asList("TASK-" + requirementNumber + "-" + index);
    }

    /**
     * Infers a Java-style base name from the requirement title.
     */
    private static String inferBaseName(String title) {
        if (title == null || title.trim().isEmpty()) {
            return "Feature";
        }
        String[] words = title.replaceAll("[^A-Za-z0-9 ]", " ").trim().split("\\s+");
        String candidate = words[words.length - 1];
        if ("sistema".equalsIgnoreCase(candidate) && words.length > 1) {
            candidate = words[words.length - 2];
        }
        if ("puntuacion".equalsIgnoreCase(candidate) || "puntuación".equalsIgnoreCase(candidate)) {
            return "Score";
        }
        return Character.toUpperCase(candidate.charAt(0)) + candidate.substring(1).replaceAll("[^A-Za-z0-9]", "");
    }
}

