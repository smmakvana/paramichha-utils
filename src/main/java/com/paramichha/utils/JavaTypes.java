package com.paramichha.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.*;

/**
 * Pure Java type inspection utilities.
 *
 * <p>No framework assumptions — works with any Java class.
 * Covers primitive/wrapper detection, boxing/unboxing,
 * collection/temporal/numeric classification, and source name generation.
 */
public final class JavaTypes {

    private static final Set<Class<?>> WRAPPERS = Set.of(
            Boolean.class, Byte.class, Short.class, Character.class,
            Integer.class, Long.class, Float.class, Double.class
    );

    private static final Set<Class<?>> NUMERIC_TYPES = Set.of(
            byte.class, short.class, int.class, long.class,
            float.class, double.class,
            Byte.class, Short.class, Integer.class, Long.class,
            Float.class, Double.class,
            BigDecimal.class, BigInteger.class
    );

    private static final Set<Class<?>> TEMPORAL_TYPES = Set.of(
            LocalDate.class, LocalTime.class, LocalDateTime.class,
            ZonedDateTime.class, OffsetDateTime.class, Instant.class,
            Year.class, YearMonth.class, java.util.Date.class,
            java.sql.Date.class, java.sql.Timestamp.class
    );

    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER = Map.of(
            boolean.class, Boolean.class,
            byte.class,    Byte.class,
            short.class,   Short.class,
            char.class,    Character.class,
            int.class,     Integer.class,
            long.class,    Long.class,
            float.class,   Float.class,
            double.class,  Double.class,
            void.class,    Void.class
    );

    private static final Map<Class<?>, Class<?>> WRAPPER_TO_PRIMITIVE =
            PRIMITIVE_TO_WRAPPER.entrySet().stream()
                    .collect(java.util.stream.Collectors.toMap(
                            Map.Entry::getValue, Map.Entry::getKey));

    private JavaTypes() {}

    // =========================================================================
    // Primitive / wrapper
    // =========================================================================

    public static boolean isPrimitive(Class<?> type) {
        return type != null && type.isPrimitive() && type != void.class;
    }

    public static boolean isWrapper(Class<?> type) {
        return WRAPPERS.contains(type);
    }

    public static boolean isPrimitiveOrWrapper(Class<?> type) {
        return isPrimitive(type) || isWrapper(type);
    }

    /** int.class → Integer.class, already boxed types returned as-is. */
    public static Class<?> box(Class<?> type) {
        if (type == null) return null;
        return PRIMITIVE_TO_WRAPPER.getOrDefault(type, type);
    }

    /** Integer.class → int.class, already primitive types returned as-is. */
    public static Class<?> unbox(Class<?> type) {
        if (type == null) return null;
        return WRAPPER_TO_PRIMITIVE.getOrDefault(type, type);
    }

    // =========================================================================
    // Common type checks
    // =========================================================================

    public static boolean isString(Class<?> type) {
        return type == String.class;
    }

    public static boolean isBoolean(Class<?> type) {
        return type == boolean.class || type == Boolean.class;
    }

    public static boolean isNumeric(Class<?> type) {
        return NUMERIC_TYPES.contains(type);
    }

    public static boolean isTemporal(Class<?> type) {
        return TEMPORAL_TYPES.contains(type);
    }

    public static boolean isEnum(Class<?> type) {
        return type != null && type.isEnum();
    }

    public static boolean isRecord(Class<?> type) {
        return type != null && type.isRecord();
    }

    public static boolean isArray(Class<?> type) {
        return type != null && type.isArray();
    }

    public static boolean isInterface(Class<?> type) {
        return type != null && type.isInterface();
    }

    public static boolean isAbstract(Class<?> type) {
        return type != null && java.lang.reflect.Modifier.isAbstract(type.getModifiers());
    }

    // =========================================================================
    // Collection / map / optional
    // =========================================================================

    public static boolean isCollection(Class<?> type) {
        return type != null && Collection.class.isAssignableFrom(type);
    }

    public static boolean isList(Class<?> type) {
        return type != null && List.class.isAssignableFrom(type);
    }

    public static boolean isSet(Class<?> type) {
        return type != null && Set.class.isAssignableFrom(type);
    }

    public static boolean isQueue(Class<?> type) {
        return type != null && Queue.class.isAssignableFrom(type);
    }

    public static boolean isMap(Class<?> type) {
        return type != null && Map.class.isAssignableFrom(type);
    }

    public static boolean isOptional(Class<?> type) {
        return type == Optional.class;
    }

    // =========================================================================
    // Package classification
    // =========================================================================

    /** Returns true for types in java.* or jakarta.* packages. */
    public static boolean isJavaType(Class<?> type) {
        if (type == null) return false;
        String pkg = type.getPackageName();
        return pkg.startsWith("java.") || pkg.startsWith("jakarta.");
    }

    /** Returns true for types outside java.* and jakarta.* — user-defined domain types. */
    public static boolean isDomainType(Class<?> type) {
        return type != null && !type.isPrimitive() && !isJavaType(type) && !type.isEnum();
    }

    // =========================================================================
    // Source name generation
    // =========================================================================

    /**
     * Returns the Java source type name for use in generated code.
     *
     * <pre>
     * JavaTypes.sourceName(int.class)         → "int"
     * JavaTypes.sourceName(String.class)      → "String"
     * JavaTypes.sourceName(BigDecimal.class)  → "BigDecimal"
     * JavaTypes.sourceName(String[].class)    → "String[]"
     * JavaTypes.sourceName(Inner.class)       → "Outer.Inner"
     * </pre>
     */
    public static String sourceName(Class<?> type) {
        if (type == null) return "Object";
        if (type.isArray()) return sourceName(type.getComponentType()) + "[]";
        if (type.getEnclosingClass() != null)
            return type.getEnclosingClass().getSimpleName() + "." + type.getSimpleName();
        return type.getSimpleName();
    }

    /**
     * Returns the boxed source name — primitives become their wrapper name.
     *
     * <pre>
     * JavaTypes.boxedSourceName(int.class)   → "Integer"
     * JavaTypes.boxedSourceName(String.class) → "String"
     * </pre>
     */
    public static String boxedSourceName(Class<?> type) {
        return sourceName(box(type));
    }
}