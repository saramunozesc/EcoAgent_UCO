package es.uco.ecoagent.llm.requirements;

import java.util.List;
import java.util.Map;

/**
 * Minimal object wrapper used by the requirements JSON parser.
 */
final class JsonObject {
    private final Map<String, Object> values;

    /**
     * Stores parsed JSON object values by field name.
     */
    JsonObject(Map<String, Object> values) {
        this.values = values;
    }

    /**
     * Returns a required string field or fails validation.
     */
    String requiredString(String key) {
        Object value = values.get(key);
        if (!(value instanceof String)) {
            throw new RequirementValidationException("Missing string field: " + key);
        }
        return (String) value;
    }

    /**
     * Returns a required array field or fails validation.
     */
    @SuppressWarnings("unchecked")
    List<Object> requiredArray(String key) {
        Object value = values.get(key);
        if (!(value instanceof List)) {
            throw new RequirementValidationException("Missing array field: " + key);
        }
        return (List<Object>) value;
    }

    /**
     * Returns a required string array field or fails validation.
     */
    List<String> requiredStringArray(String key) {
        List<Object> raw = requiredArray(key);
        java.util.ArrayList<String> strings = new java.util.ArrayList<String>();
        for (Object item : raw) {
            if (!(item instanceof String)) {
                throw new RequirementValidationException("Array must contain only strings: " + key);
            }
            strings.add((String) item);
        }
        return strings;
    }
}

