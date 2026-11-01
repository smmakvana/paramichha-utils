package com.paramichha.utils;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UtilsTest {

    @Nested
    class SeedsTest {
        /**
         * Golden values. TestKit seeds generated tests from Seeds.stableHash(className),
         * so a change here changes every generated test file. Only update deliberately.
         */
        @Test
        void stableHashIsFrozen() {
            assertThat(Seeds.stableHash("abc")).isEqualTo(-3351804022671199651L);
            assertThat(Seeds.stableHash(String.class)).isEqualTo(7465538609921379402L);
            assertThat(Seeds.stableHash("a", "bc")).isEqualTo(7096547112149444987L);
        }

        @Test
        void partsAreSeparated() {
            assertThat(Seeds.stableHash("a", "bc")).isNotEqualTo(Seeds.stableHash("ab", "c"));
        }

        @Test
        void nullIsZeroAndSameSeedSameRandom() {
            assertThat(Seeds.stableHash((String) null)).isZero();
            assertThat(Seeds.random(42).nextLong()).isEqualTo(Seeds.random(42).nextLong());
        }
    }

    @Nested
    class TextsTest {
        @Test
        void caseConversions() {
            for (String in : List.of("hello world", "hello_world", "hello-world", "HelloWorld")) {
                assertThat(Texts.camelCase(in)).as(in).isEqualTo("helloWorld");
                assertThat(Texts.pascalCase(in)).as(in).isEqualTo("HelloWorld");
                assertThat(Texts.snakeCase(in)).as(in).isEqualTo("hello_world");
                assertThat(Texts.kebabCase(in)).as(in).isEqualTo("hello-world");
            }
        }

        @Test
        void blanksAndNulls() {
            assertThat(Texts.isBlank("  ")).isTrue();
            assertThat(Texts.isBlank(null)).isTrue();
            assertThat(Texts.isNotBlank("x")).isTrue();
            assertThat(Texts.capitalize(null)).isNull();
            assertThat(Texts.capitalize("abc")).isEqualTo("Abc");
            assertThat(Texts.camelCase("")).isEmpty();
        }
    }

    @Nested
    class JavaTypesTest {
        @Test
        void boxingAndNames() {
            assertThat(JavaTypes.box(int.class)).isEqualTo(Integer.class);
            assertThat(JavaTypes.unbox(Integer.class)).isEqualTo(int.class);
            assertThat(JavaTypes.box(String.class)).isEqualTo(String.class);
            assertThat(JavaTypes.sourceName(int[].class)).isEqualTo("int[]");
            assertThat(JavaTypes.boxedSourceName(int.class)).isEqualTo("Integer");
        }

        @Test
        void domainTypes() {
            assertThat(JavaTypes.isDomainType(UtilsTest.class)).isTrue();
            assertThat(JavaTypes.isDomainType(String.class)).isFalse();
            assertThat(JavaTypes.isList(List.class)).isTrue();
            assertThat(JavaTypes.isMap(Map.class)).isTrue();
        }
    }

    @Nested
    class TokensTest {
        @Test
        void resolvesKnownTokensAndKeepsUnknownOnes() {
            assertThat(Tokens.resolve("Hi {name}, {n}!", "name", "Ada", "n", "3")).isEqualTo("Hi Ada, 3!");
            assertThat(Tokens.resolve("Hi {name} {missing}", Map.of("name", "Ada"))).isEqualTo("Hi Ada {missing}");
        }
    }

    @Nested
    class GuardsTest {
        @Test
        void guardsReturnValidValues() {
            assertThat(Guard.notBlank("x", "name")).isEqualTo("x");
            assertThat(Guard.inRange(2, 1, 3, "n")).isEqualTo(2);
            assertThat(DataValidation.email("a@b.co")).isEqualTo("a@b.co");
        }

        @Test
        void guardsRejectInvalidValuesWithReadableMessages() {
            assertThatThrownBy(() -> Guard.notBlank(" ", "x")).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("x must not be blank");
            assertThatThrownBy(() -> Guard.inRange(5, 1, 3, "x")).hasMessage("x must be between 1 and 3, got: 5");
            assertThatThrownBy(() -> Guard.positive(0, "x")).hasMessage("x must be positive, got: 0");
            assertThatThrownBy(() -> DataValidation.min(1, 0)).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> DataValidation.email("bad")).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> DataValidation.notNull(null, "x must not be null")).hasMessage("x must not be null");
        }
    }
}
