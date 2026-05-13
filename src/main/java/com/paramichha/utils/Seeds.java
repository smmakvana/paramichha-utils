package com.paramichha.utils;

import java.util.Random;

public final class Seeds {

    private Seeds() {}

    /**
     * Produces a stable hash for a string value.
     * Unlike {@link String#hashCode()}, this is guaranteed stable
     * across JVM versions and implementations.
     */
    public static long stableHash(String value) {
        if (value == null) return 0L;
        long hash = 1125899906842597L;
        for (char c : value.toCharArray()) {
            hash = 31 * hash + c;
        }
        return hash;
    }

    /**
     * Stable hash based on the class's fully-qualified name.
     * Safe for use as a DataFactory seed across JVM versions.
     */
    public static long stableHash(Class<?> type) {
        return stableHash(type.getName());
    }

    /**
     * Combines multiple string parts into one stable hash.
     * Use for method-level or field-level seeds:
     * <pre>
     * Seeds.stableHash("Circle", "resize")  → deterministic per method
     * </pre>
     */
    public static long stableHash(String... parts) {
        long hash = 1125899906842597L;
        for (String part : parts) {
            if (part != null) {
                for (char c : part.toCharArray()) {
                    hash = 31 * hash + c;
                }
            }
            hash = 31 * hash + '|'; // separator between parts
        }
        return hash;
    }

    public static Random random(long seed) {
        return new Random(seed);
    }
}
