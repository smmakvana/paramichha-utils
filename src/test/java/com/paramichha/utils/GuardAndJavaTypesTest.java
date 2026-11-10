package com.paramichha.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GuardAndJavaTypesTest {

    @Nested
    @DisplayName("JavaTypes")
    class JavaTypesTest {
        enum Colour { RED }
        record Point(int x) { }
        static class Inner { }

        @Test
        void primitivesAndWrappers() {
            assertThat(JavaTypes.isPrimitive(int.class)).isTrue();
            assertThat(JavaTypes.isPrimitive(void.class)).isFalse();
            assertThat(JavaTypes.isPrimitive(Integer.class)).isFalse();
            assertThat(JavaTypes.isPrimitive(null)).isFalse();
            assertThat(JavaTypes.isWrapper(Integer.class)).isTrue();
            assertThat(JavaTypes.isWrapper(String.class)).isFalse();
            assertThat(JavaTypes.isPrimitiveOrWrapper(int.class)).isTrue();
            assertThat(JavaTypes.isPrimitiveOrWrapper(Long.class)).isTrue();
            assertThat(JavaTypes.isPrimitiveOrWrapper(String.class)).isFalse();
            assertThat(JavaTypes.box(null)).isNull();
            assertThat(JavaTypes.unbox(null)).isNull();
            assertThat(JavaTypes.unbox(String.class)).isEqualTo(String.class);
        }

        @Test
        void kinds() {
            assertThat(JavaTypes.isString(String.class)).isTrue();
            assertThat(JavaTypes.isString(Object.class)).isFalse();
            assertThat(JavaTypes.isBoolean(boolean.class)).isTrue();
            assertThat(JavaTypes.isBoolean(Boolean.class)).isTrue();
            assertThat(JavaTypes.isBoolean(String.class)).isFalse();
            assertThat(JavaTypes.isNumeric(BigDecimal.class)).isTrue();
            assertThat(JavaTypes.isNumeric(BigInteger.class)).isTrue();
            assertThat(JavaTypes.isNumeric(String.class)).isFalse();
            assertThat(JavaTypes.isTemporal(LocalDate.class)).isTrue();
            assertThat(JavaTypes.isTemporal(String.class)).isFalse();
            assertThat(JavaTypes.isEnum(Colour.class)).isTrue();
            assertThat(JavaTypes.isEnum(String.class)).isFalse();
            assertThat(JavaTypes.isEnum(null)).isFalse();
            assertThat(JavaTypes.isRecord(Point.class)).isTrue();
            assertThat(JavaTypes.isRecord(String.class)).isFalse();
            assertThat(JavaTypes.isRecord(null)).isFalse();
            assertThat(JavaTypes.isArray(int[].class)).isTrue();
            assertThat(JavaTypes.isArray(String.class)).isFalse();
            assertThat(JavaTypes.isArray(null)).isFalse();
            assertThat(JavaTypes.isInterface(Serializable.class)).isTrue();
            assertThat(JavaTypes.isInterface(String.class)).isFalse();
            assertThat(JavaTypes.isInterface(null)).isFalse();
            assertThat(JavaTypes.isAbstract(AbstractList.class)).isTrue();
            assertThat(JavaTypes.isAbstract(String.class)).isFalse();
            assertThat(JavaTypes.isAbstract(null)).isFalse();
        }

        @Test
        void collections() {
            assertThat(JavaTypes.isCollection(Set.class)).isTrue();
            assertThat(JavaTypes.isCollection(Map.class)).isFalse();
            assertThat(JavaTypes.isCollection(null)).isFalse();
            assertThat(JavaTypes.isList(Set.class)).isFalse();
            assertThat(JavaTypes.isList(null)).isFalse();
            assertThat(JavaTypes.isSet(HashSet.class)).isTrue();
            assertThat(JavaTypes.isSet(List.class)).isFalse();
            assertThat(JavaTypes.isSet(null)).isFalse();
            assertThat(JavaTypes.isQueue(ArrayDeque.class)).isTrue();
            assertThat(JavaTypes.isQueue(List.class)).isFalse();
            assertThat(JavaTypes.isQueue(null)).isFalse();
            assertThat(JavaTypes.isMap(HashMap.class)).isTrue();
            assertThat(JavaTypes.isMap(List.class)).isFalse();
            assertThat(JavaTypes.isMap(null)).isFalse();
            assertThat(JavaTypes.isOptional(Optional.class)).isTrue();
            assertThat(JavaTypes.isOptional(List.class)).isFalse();
        }

        @Test
        void packageClassification() {
            assertThat(JavaTypes.isJavaType(String.class)).isTrue();
            assertThat(JavaTypes.isJavaType(jakarta.paramichha.fixture.JakartaType.class)).isTrue();
            assertThat(JavaTypes.isDomainType(jakarta.paramichha.fixture.JakartaType.class)).isFalse();
            assertThat(JavaTypes.isJavaType(Inner.class)).isFalse();
            assertThat(JavaTypes.isJavaType(null)).isFalse();
            assertThat(JavaTypes.isDomainType(Inner.class)).isTrue();
            assertThat(JavaTypes.isDomainType(int.class)).isFalse();
            assertThat(JavaTypes.isDomainType(Colour.class)).isFalse();
            assertThat(JavaTypes.isDomainType(null)).isFalse();
        }

        @Test
        void sourceNames() {
            assertThat(JavaTypes.sourceName(null)).isEqualTo("Object");
            assertThat(JavaTypes.sourceName(String.class)).isEqualTo("String");
            assertThat(JavaTypes.sourceName(Inner.class)).isEqualTo("JavaTypesTest.Inner");
            assertThat(JavaTypes.sourceName(Inner[].class)).isEqualTo("JavaTypesTest.Inner[]");
            assertThat(JavaTypes.boxedSourceName(long.class)).isEqualTo("Long");
        }
    }
}
