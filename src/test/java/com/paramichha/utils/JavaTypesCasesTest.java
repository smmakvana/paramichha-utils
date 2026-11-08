package com.paramichha.utils;

import static com.paramichha.utils.Cases.Returns;
import static com.paramichha.utils.Cases.ok;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.AbstractList;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/** JavaTypes cases: primitives, wrappers, type kinds, collections, package classification, names. */
@DisplayName("JavaTypes cases")
class JavaTypesCasesTest {

    enum Colour {
        RED
    }

    record Point(int x) {}

    static class Inner {}

    static Stream<Returns> returns() {
        return Stream.of(
                // primitives and wrappers
                ok("int is primitive", () -> JavaTypes.isPrimitive(int.class), true),
                ok("void is not primitive", () -> JavaTypes.isPrimitive(void.class), false),
                ok("Integer is not primitive", () -> JavaTypes.isPrimitive(Integer.class), false),
                ok("null is not primitive", () -> JavaTypes.isPrimitive(null), false),
                ok("Integer is a wrapper", () -> JavaTypes.isWrapper(Integer.class), true),
                ok("String is not a wrapper", () -> JavaTypes.isWrapper(String.class), false),
                ok("int is primitive or wrapper", () -> JavaTypes.isPrimitiveOrWrapper(int.class), true),
                ok("Long is primitive or wrapper", () -> JavaTypes.isPrimitiveOrWrapper(Long.class), true),
                ok("String is neither", () -> JavaTypes.isPrimitiveOrWrapper(String.class), false),
                ok("box of null is null", () -> JavaTypes.box(null), null),
                ok("unbox of null is null", () -> JavaTypes.unbox(null), null),
                ok("unbox of a non-wrapper stays", () -> JavaTypes.unbox(String.class), String.class),
                ok("box of int is Integer", () -> JavaTypes.box(int.class), Integer.class),
                ok("box of String stays", () -> JavaTypes.box(String.class), String.class),
                ok("unbox of Integer is int", () -> JavaTypes.unbox(Integer.class), int.class),

                // kinds
                ok("String is a string", () -> JavaTypes.isString(String.class), true),
                ok("Object is not a string", () -> JavaTypes.isString(Object.class), false),
                ok("boolean is boolean", () -> JavaTypes.isBoolean(boolean.class), true),
                ok("Boolean is boolean", () -> JavaTypes.isBoolean(Boolean.class), true),
                ok("String is not boolean", () -> JavaTypes.isBoolean(String.class), false),
                ok("BigDecimal is numeric", () -> JavaTypes.isNumeric(BigDecimal.class), true),
                ok("BigInteger is numeric", () -> JavaTypes.isNumeric(BigInteger.class), true),
                ok("String is not numeric", () -> JavaTypes.isNumeric(String.class), false),
                ok("LocalDate is temporal", () -> JavaTypes.isTemporal(LocalDate.class), true),
                ok("String is not temporal", () -> JavaTypes.isTemporal(String.class), false),
                ok("an enum is an enum", () -> JavaTypes.isEnum(Colour.class), true),
                ok("String is not an enum", () -> JavaTypes.isEnum(String.class), false),
                ok("null is not an enum", () -> JavaTypes.isEnum(null), false),
                ok("a record is a record", () -> JavaTypes.isRecord(Point.class), true),
                ok("String is not a record", () -> JavaTypes.isRecord(String.class), false),
                ok("null is not a record", () -> JavaTypes.isRecord(null), false),
                ok("an int array is an array", () -> JavaTypes.isArray(int[].class), true),
                ok("String is not an array", () -> JavaTypes.isArray(String.class), false),
                ok("null is not an array", () -> JavaTypes.isArray(null), false),
                ok("Serializable is an interface", () -> JavaTypes.isInterface(Serializable.class), true),
                ok("String is not an interface", () -> JavaTypes.isInterface(String.class), false),
                ok("null is not an interface", () -> JavaTypes.isInterface(null), false),
                ok("AbstractList is abstract", () -> JavaTypes.isAbstract(AbstractList.class), true),
                ok("String is not abstract", () -> JavaTypes.isAbstract(String.class), false),
                ok("null is not abstract", () -> JavaTypes.isAbstract(null), false),

                // collections
                ok("Set is a collection", () -> JavaTypes.isCollection(Set.class), true),
                ok("Map is not a collection", () -> JavaTypes.isCollection(Map.class), false),
                ok("null is not a collection", () -> JavaTypes.isCollection(null), false),
                ok("Set is not a list", () -> JavaTypes.isList(Set.class), false),
                ok("null is not a list", () -> JavaTypes.isList(null), false),
                ok("HashSet is a set", () -> JavaTypes.isSet(HashSet.class), true),
                ok("List is not a set", () -> JavaTypes.isSet(List.class), false),
                ok("null is not a set", () -> JavaTypes.isSet(null), false),
                ok("ArrayDeque is a queue", () -> JavaTypes.isQueue(ArrayDeque.class), true),
                ok("List is not a queue", () -> JavaTypes.isQueue(List.class), false),
                ok("null is not a queue", () -> JavaTypes.isQueue(null), false),
                ok("HashMap is a map", () -> JavaTypes.isMap(HashMap.class), true),
                ok("List is not a map", () -> JavaTypes.isMap(List.class), false),
                ok("null is not a map", () -> JavaTypes.isMap(null), false),
                ok("Optional is an optional", () -> JavaTypes.isOptional(Optional.class), true),
                ok("List is not an optional", () -> JavaTypes.isOptional(List.class), false),

                // package classification
                ok("String is a Java type", () -> JavaTypes.isJavaType(String.class), true),
                ok("a non-Java class is not a Java type", () -> JavaTypes.isJavaType(Inner.class), false),
                ok("null is not a Java type", () -> JavaTypes.isJavaType(null), false),
                ok("a nested class is a domain type", () -> JavaTypes.isDomainType(Inner.class), true),
                ok("int is not a domain type", () -> JavaTypes.isDomainType(int.class), false),
                ok("an enum is not a domain type", () -> JavaTypes.isDomainType(Colour.class), false),
                ok("null is not a domain type", () -> JavaTypes.isDomainType(null), false),

                // source names
                ok("null has the name Object", () -> JavaTypes.sourceName(null), "Object"),
                ok("String's source name", () -> JavaTypes.sourceName(String.class), "String"),
                ok("a nested class's source name", () -> JavaTypes.sourceName(Inner.class), "JavaTypesCasesTest.Inner"),
                ok(
                        "an array of a nested class",
                        () -> JavaTypes.sourceName(Inner[].class),
                        "JavaTypesCasesTest.Inner[]"),
                ok("int[] source name", () -> JavaTypes.sourceName(int[].class), "int[]"),
                ok("boxed name of long", () -> JavaTypes.boxedSourceName(long.class), "Long"),
                ok("boxed name of int", () -> JavaTypes.boxedSourceName(int.class), "Integer"),
                ok("boxed name of a non-primitive", () -> JavaTypes.boxedSourceName(String.class), "String"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("returns")
    @DisplayName("returns the expected value")
    void returnsExpected(Returns row) throws Throwable {
        assertThat(row.call().get()).as(row.name()).isEqualTo(row.expected());
    }
}
