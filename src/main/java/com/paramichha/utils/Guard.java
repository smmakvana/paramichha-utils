package com.paramichha.utils;

import java.util.Collection;

public final class Guard {

    private Guard() {}

    // =========================================================================
    // Null checks
    // =========================================================================

    /**
     * Throws {@link IllegalArgumentException} if value is null.
     *
     * @return the value — allows inline use: {@code this.x = Guard.notNull(x, "x")}
     */
    public static <T> T notNull(T value, String name) {
        if (value == null)
            throw new IllegalArgumentException(name + " must not be null");
        return value;
    }

    // =========================================================================
    // String checks
    // =========================================================================

    /**
     * Throws if value is null or blank (whitespace only).
     *
     * @return the value trimmed
     */
    public static String notBlank(String value, String name) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException(name + " must not be blank");
        return value.trim();
    }

    /**
     * Throws if value is null or empty (length zero, whitespace allowed).
     *
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
     *
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
     *
     * @return the value
     */
    public static int positive(int value, String name) {
        if (value <= 0)
            throw new IllegalArgumentException(name + " must be positive, got: " + value);
        return value;
    }

    /**
     * Throws if value is zero or negative.
     *
     * @return the value
     */
    public static long positive(long value, String name) {
        if (value <= 0)
            throw new IllegalArgumentException(name + " must be positive, got: " + value);
        return value;
    }

    /**
     * Throws if value is negative.
     *
     * @return the value
     */
    public static int notNegative(int value, String name) {
        if (value < 0)
            throw new IllegalArgumentException(name + " must not be negative, got: " + value);
        return value;
    }

    /**
     * Throws if value is outside [min, max] inclusive.
     *
     * @return the value
     */
    public static int inRange(int value, int min, int max, String name) {
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
     * Use for invariant checks, not input validation.
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
}