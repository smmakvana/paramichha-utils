package com.paramichha.utils;

import java.util.Locale;

public final class Texts {

    private Texts() {}

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isNotBlank(String value) {
        return !isBlank(value);
    }

    public static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    public static String emptyToNull(String value) {
        return isBlank(value) ? null : value;
    }

    public static String safeTrim(String value) {
        return value == null ? null : value.trim();
    }

    /**
     * Upper-cases the first character. A null or blank value is returned unchanged.
     */
    public static String capitalize(String value) {
        if (isBlank(value)) {
            return value;
        }
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    /**
     * Lower-cases the first character. A null or blank value is returned unchanged.
     */
    public static String decapitalize(String value) {
        if (isBlank(value)) {
            return value;
        }

        return Character.toLowerCase(value.charAt(0)) + value.substring(1);
    }

    /**
     * Turns words separated by spaces, underscores, hyphens or camel-case humps into one camel-case
     * name, for example {@code "order_item id"} into {@code "orderItemId"}. A null or blank value is
     * returned unchanged.
     */
    public static String camelCase(String value) {
        if (isBlank(value)) {
            return value;
        }

        String normalized = normalizeSeparators(value);
        String[] parts = normalized.split(" ");

        StringBuilder result = new StringBuilder(parts[0].toLowerCase(Locale.ROOT));

        for (int i = 1; i < parts.length; i++) {
            result.append(capitalize(parts[i].toLowerCase(Locale.ROOT)));
        }

        return result.toString();
    }

    public static String pascalCase(String value) {
        return capitalize(camelCase(value));
    }

    public static String snakeCase(String value) {
        return splitCamelCase(value).replaceAll("[\\s-]+", "_").toLowerCase(Locale.ROOT);
    }

    public static String kebabCase(String value) {
        return splitCamelCase(value).replaceAll("[\\s_]+", "-").toLowerCase(Locale.ROOT);
    }

    /**
     * Removes every character that is not an ASCII letter or digit and lower-cases the rest. A null
     * or blank value gives an empty string.
     */
    public static String normalize(String value) {
        if (isBlank(value)) {
            return "";
        }

        return value.replaceAll("[^a-zA-Z0-9]", "").toLowerCase(Locale.ROOT);
    }

    public static boolean equalsIgnoreCaseTrimmed(String a, String b) {
        return safeTrim(nullToEmpty(a)).equalsIgnoreCase(safeTrim(nullToEmpty(b)));
    }

    /**
     * Whether {@code text} contains {@code search}, ignoring case. False when either is null.
     */
    public static boolean containsIgnoreCase(String text, String search) {
        if (text == null || search == null) {
            return false;
        }

        return text.toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT));
    }

    private static String normalizeSeparators(String value) {
        return splitCamelCase(value).replaceAll("[_-]", " ").trim();
    }

    private static String splitCamelCase(String value) {
        return value.replaceAll("([a-z])([A-Z])", "$1 $2");
    }
}
