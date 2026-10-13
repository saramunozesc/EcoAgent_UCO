package es.uco.ecoagent.llm.requirements;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Small JSON parser for the strict requirements schema used in phase 2.
 */
final class JsonCursor {
    private final String text;
    private int index;

    /**
     * Creates a parser over one JSON document.
     */
    JsonCursor(String text) {
        this.text = text == null ? "" : text.trim();
    }

    /**
     * Parses a value and requires the full document to be consumed.
     */
    Object parseValue() {
        Object value = parseAny();
        skipWhitespace();
        if (index != text.length()) {
            throw new RequirementValidationException("Unexpected JSON content at position " + index);
        }
        return value;
    }

    /**
     * Parses one JSON value supported by the phase 2 schema.
     */
    private Object parseAny() {
        skipWhitespace();
        if (peek('{')) {
            return parseObject();
        }
        if (peek('[')) {
            return parseArray();
        }
        if (peek('"')) {
            return parseString();
        }
        throw new RequirementValidationException("Unexpected JSON token at position " + index);
    }

    /**
     * Parses a JSON object into insertion-ordered fields.
     */
    private JsonObject parseObject() {
        expect('{');
        Map<String, Object> values = new LinkedHashMap<String, Object>();
        skipWhitespace();
        if (peek('}')) {
            expect('}');
            return new JsonObject(values);
        }
        while (true) {
            String key = parseString();
            skipWhitespace();
            expect(':');
            values.put(key, parseAny());
            skipWhitespace();
            if (peek('}')) {
                expect('}');
                return new JsonObject(values);
            }
            expect(',');
        }
    }

    /**
     * Parses a JSON array.
     */
    private List<Object> parseArray() {
        expect('[');
        List<Object> values = new ArrayList<Object>();
        skipWhitespace();
        if (peek(']')) {
            expect(']');
            return values;
        }
        while (true) {
            values.add(parseAny());
            skipWhitespace();
            if (peek(']')) {
                expect(']');
                return values;
            }
            expect(',');
        }
    }

    /**
     * Parses a JSON string with common escape sequences.
     */
    private String parseString() {
        expect('"');
        StringBuilder builder = new StringBuilder();
        while (index < text.length()) {
            char current = text.charAt(index++);
            if (current == '"') {
                return builder.toString();
            }
            if (current == '\\') {
                if (index >= text.length()) {
                    throw new RequirementValidationException("Invalid JSON escape");
                }
                char escaped = text.charAt(index++);
                if (escaped == '"' || escaped == '\\' || escaped == '/') {
                    builder.append(escaped);
                } else if (escaped == 'n') {
                    builder.append('\n');
                } else if (escaped == 'r') {
                    builder.append('\r');
                } else if (escaped == 't') {
                    builder.append('\t');
                } else {
                    throw new RequirementValidationException("Unsupported JSON escape: " + escaped);
                }
            } else {
                builder.append(current);
            }
        }
        throw new RequirementValidationException("Unclosed JSON string");
    }

    /**
     * Consumes the expected character after whitespace.
     */
    private void expect(char expected) {
        skipWhitespace();
        if (index >= text.length() || text.charAt(index) != expected) {
            throw new RequirementValidationException("Expected '" + expected + "' at position " + index);
        }
        index++;
    }

    /**
     * Returns whether the next non-whitespace character matches.
     */
    private boolean peek(char expected) {
        skipWhitespace();
        return index < text.length() && text.charAt(index) == expected;
    }

    /**
     * Advances past JSON whitespace.
     */
    private void skipWhitespace() {
        while (index < text.length() && Character.isWhitespace(text.charAt(index))) {
            index++;
        }
    }
}

