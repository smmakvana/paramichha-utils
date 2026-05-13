package com.paramichha.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public final class Tokens {

    private Tokens() {}

    /**
     * Resolves all {key} tokens in a template using the provided map.
     * <pre>
     * Tokens.resolve("Hello {name}!", Map.of("name", "Circle"))
     * → "Hello Circle!"
     * </pre>
     */
    public static String resolve(String template, Map<String, ?> values) {
        if (template == null || values == null) return template;
        String result = template;
        for (Map.Entry<String, ?> entry : values.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
        }
        return result;
    }

    /**
     * Resolves tokens using alternating key-value pairs.
     * Convenience overload — avoids creating a Map at call site.
     * <pre>
     * Tokens.resolve("{ClassName}Test", "ClassName", "Circle")
     * → "CircleTest"
     *
     * Tokens.resolve("{ClassName}{MemberName}Test",
     *     "ClassName", "Circle", "MemberName", "Radius")
     * → "CircleRadiusTest"
     * </pre>
     *
     * @param template the template string
     * @param keyValues alternating key, value, key, value...
     * @throws IllegalArgumentException if keyValues length is not even
     */
    public static String resolve(String template, String... keyValues) {
        if (template == null) return null;
        if (keyValues == null || keyValues.length == 0) return template;
        if (keyValues.length % 2 != 0) {
            throw new IllegalArgumentException(
                    "keyValues must be alternating key-value pairs, got " + keyValues.length + " args");
        }
        Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            map.put(keyValues[i], keyValues[i + 1] != null ? keyValues[i + 1] : "");
        }
        return resolve(template, map);
    }
}
