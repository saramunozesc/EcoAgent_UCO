package es.uco.ecoagent.llm.requirements;

import es.uco.ecoagent.core.domain.RequirementPriority;

import java.util.ArrayList;
import java.util.List;

/**
 * Serializes validated requirement specifications to stable JSON.
 */
public final class RequirementsJson {
    /**
     * Prevents instantiation because JSON helpers are static.
     */
    private RequirementsJson() {
    }

    /**
     * Serializes a spec with deterministic field ordering.
     */
    public static String toJson(RequirementSpec spec) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        builder.append("  \"projectName\": \"").append(escape(spec.getProjectName())).append("\",\n");
        builder.append("  \"actors\": ").append(stringArray(spec.getActors())).append(",\n");
        builder.append("  \"requirements\": [\n");
        for (int index = 0; index < spec.getRequirements().size(); index++) {
            RequirementItem item = spec.getRequirements().get(index);
            builder.append("    {\n");
            builder.append("      \"id\": \"").append(escape(item.getId())).append("\",\n");
            builder.append("      \"title\": \"").append(escape(item.getTitle())).append("\",\n");
            builder.append("      \"acceptanceCriteria\": ").append(stringArray(item.getAcceptanceCriteria())).append(",\n");
            builder.append("      \"priority\": \"").append(item.getPriority().name()).append("\"\n");
            builder.append("    }");
            if (index + 1 < spec.getRequirements().size()) {
                builder.append(",");
            }
            builder.append("\n");
        }
        builder.append("  ]\n");
        builder.append("}\n");
        return builder.toString();
    }

    /**
     * Parses and validates the expected requirements JSON schema.
     */
    public static RequirementSpec parseAndValidate(String json) {
        JsonCursor cursor = new JsonCursor(json);
        Object root = cursor.parseValue();
        if (!(root instanceof JsonObject)) {
            throw new RequirementValidationException("Root must be a JSON object");
        }
        JsonObject object = (JsonObject) root;
        String projectName = object.requiredString("projectName");
        List<String> actors = object.requiredStringArray("actors");
        List<Object> rawRequirements = object.requiredArray("requirements");
        if (projectName.trim().isEmpty() || actors.isEmpty() || rawRequirements.isEmpty()) {
            throw new RequirementValidationException("projectName, actors and requirements are required");
        }

        List<RequirementItem> items = new ArrayList<RequirementItem>();
        for (int index = 0; index < rawRequirements.size(); index++) {
            Object raw = rawRequirements.get(index);
            if (!(raw instanceof JsonObject)) {
                throw new RequirementValidationException("Requirement must be an object");
            }
            JsonObject requirement = (JsonObject) raw;
            String title = requirement.requiredString("title");
            List<String> criteria = requirement.requiredStringArray("acceptanceCriteria");
            RequirementPriority priority;
            String priorityName = requirement.requiredString("priority");
            try {
                priority = RequirementPriority.valueOf(priorityName);
            } catch (IllegalArgumentException exception) {
                throw new RequirementValidationException("Invalid requirement priority: " + priorityName);
            }
            if (title.trim().isEmpty() || criteria.isEmpty()) {
                throw new RequirementValidationException("Requirement title and criteria are required");
            }
            items.add(new RequirementItem(String.format("REQ-%03d", index + 1), title, criteria, priority));
        }

        return new RequirementSpec(projectName, actors, items);
    }

    /**
     * Serializes a list of strings as a JSON array.
     */
    private static String stringArray(List<String> values) {
        StringBuilder builder = new StringBuilder("[");
        for (int index = 0; index < values.size(); index++) {
            if (index > 0) {
                builder.append(", ");
            }
            builder.append("\"").append(escape(values.get(index))).append("\"");
        }
        builder.append("]");
        return builder.toString();
    }

    /**
     * Escapes characters needed by the small JSON writer.
     */
    static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}

