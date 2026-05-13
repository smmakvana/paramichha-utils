package com.paramichha.utils;

import java.util.Collection;

/**
 * Input validation guards for conditions Java does not cover natively.
 *
 * <p>For null checks use {@link java.util.Objects#requireNonNull} directly.
 * Everything else belongs here.
 *
 * <pre>
 * Objects.requireNonNull(type, "type");    // null  — use Java
 * Guard.notBlank(name, "name");            // blank — use Guard
 * Guard.positive(count, "count");          // numeric — use Guard
 * </pre>
 */
public final class Guard {

    private Guard() {}

    // =========================================================================
    // String checks
    // =========================================================================

    /**
     * Throws if value is null or blank (whitespace only).
     * @return the trimmed value
     */
    public static String notBlank(String value, String name) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException(name + " must not be blank");
        return value.trim();
    }

    /**
     * Throws if value is null or empty (length zero, whitespace allowed).
     * @return the value
     */
    public static String notEmpty(String value, String name) {
        if (value == null || value.isEmpty())
            throw new IllegalArgumentException(name + " must not be empty");
        return value;
    }

    // =========================================================================
    // Collection checks
    // =========================================================================

    /**
     * Throws if collection is null or empty.
     * @return the collection
     */
    public static <T extends Collection<?>> T notEmpty(T value, String name) {
        if (value == null || value.isEmpty())
            throw new IllegalArgumentException(name + " must not be empty");
        return value;
    }

    // =========================================================================
    // Numeric checks
    // =========================================================================

    /**
     * Throws if value is zero or negative.
     * @return the value
     */
    public static int positive(int value, String name) {
        if (value <= 0)
            throw new IllegalArgumentException(name + " must be positive, got: " + value);
        return value;
    }

    /**
     * Throws if value is zero or negative.
     * @return the value
     */
    public static long positive(long value, String name) {
        if (value <= 0)
            throw new IllegalArgumentException(name + " must be positive, got: " + value);
        return value;
    }

    /**
     * Throws if value is negative.
     * @return the value
     */
    public static int notNegative(int value, String name) {
        if (value < 0)
            throw new IllegalArgumentException(name + " must not be negative, got: " + value);
        return value;
    }

    /**
     * Throws if value is outside [min, max] inclusive.
     * @return the value
     */
    public static int inRange(int value, int min, int max, String name) {
        if (value < min || value > max)
            throw new IllegalArgumentException(
                    name + " must be between " + min + " and " + max + ", got: " + value);
        return value;
    }

    /**
     * Throws if value is outside [min, max] inclusive.
     * @return the value
     */
    public static long inRange(long value, long min, long max, String name) {
        if (value < min || value > max)
            throw new IllegalArgumentException(
                    name + " must be between " + min + " and " + max + ", got: " + value);
        return value;
    }

    // =========================================================================
    // State checks
    // =========================================================================

    /**
     * Throws {@link IllegalStateException} if condition is false.
     */
    public static void state(boolean condition, String message) {
        if (!condition)
            throw new IllegalStateException(message);
    }

    /**
     * Throws {@link IllegalArgumentException} if condition is false.
     */
    public static void argument(boolean condition, String message) {
        if (!condition)
            throw new IllegalArgumentException(message);
    }

    // =========================================================================
    // Future: date checks, etc.
    // =========================================================================
}