package com.paramichha.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SeedsTokensTextsTest {

    @Nested
    @DisplayName("Seeds")
    class SeedsTest {
        @Test
        void sameInputSameHashDifferentInputDifferentHash() {
            assertThat(Seeds.stableHash("abc")).isEqualTo(Seeds.stableHash("abc"));
            assertThat(Seeds.stableHash("abc")).isNotEqualTo(Seeds.stableHash("abd"));
            assertThat(Seeds.stableHash((String) null)).isZero();
        }

        @Test
        void partsHashIgnoresNullPartsButKeepsTheSeparator() {
            assertThat(Seeds.stableHash("a", null, "b")).isEqualTo(Seeds.stableHash("a", "", "b"));
            assertThat(Seeds.stableHash("a", null, "b")).isNotEqualTo(Seeds.stableHash("a", "b"));
        }

        @Test
        void randomIsSeeded() {
            assertThat(Seeds.random(42).nextLong()).isEqualTo(Seeds.random(42).nextLong());
        }
    }

    @Nested
    @DisplayName("Tokens")
    class TokensTest {
        @Test
        void mapNullTemplateOrNullValuesIsReturnedAsIs() {
            assertThat(Tokens.resolve(null, Map.of("a", 1))).isNull();
            assertThat(Tokens.resolve("{a}", (Map<String, ?>) null)).isEqualTo("{a}");
        }

        @Test
        void mapValuesAreWrittenWithStringValueOf() {
            Map<String, Object> values = new LinkedHashMap<>();
            values.put("n", 3);
            values.put("x", null);
            assertThat(Tokens.resolve("{n}-{x}-{y}", values)).isEqualTo("3-null-{y}");
        }

        @Test
        void keyValuePairs() {
            assertThat(Tokens.resolve(null, "a", "b")).isNull();
            assertThat(Tokens.resolve("{a}")).isEqualTo("{a}");
            assertThat(Tokens.resolve("{a}", (String[]) null)).isEqualTo("{a}");
            assertThat(Tokens.resolve("{a}{b}", "a", "1", "b", null)).isEqualTo("1");
            assertThatThrownBy(() -> Tokens.resolve("{a}", "a"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("keyValues must be alternating key-value pairs, got 1 args");
        }
    }

    @Nested
    @DisplayName("Texts")
    class TextsTest {
        @Test
        void blankChecks() {
            assertThat(Texts.isBlank(null)).isTrue();
            assertThat(Texts.isBlank(" \t")).isTrue();
            assertThat(Texts.isBlank("a")).isFalse();
            assertThat(Texts.isNotBlank("a")).isTrue();
            assertThat(Texts.isNotBlank(" ")).isFalse();
        }

        @Test
        void nullAndEmptyConversions() {
            assertThat(Texts.nullToEmpty(null)).isEmpty();
            assertThat(Texts.nullToEmpty("a")).isEqualTo("a");
            assertThat(Texts.emptyToNull("  ")).isNull();
            assertThat(Texts.emptyToNull("a")).isEqualTo("a");
            assertThat(Texts.safeTrim(null)).isNull();
            assertThat(Texts.safeTrim(" a ")).isEqualTo("a");
        }

        @Test
        void capitalization() {
            assertThat(Texts.capitalize("abc")).isEqualTo("Abc");
            assertThat(Texts.capitalize(" ")).isEqualTo(" ");
            assertThat(Texts.decapitalize("Abc")).isEqualTo("abc");
            assertThat(Texts.decapitalize(null)).isNull();
        }

        @Test
        void caseStyles() {
            assertThat(Texts.camelCase("first_name-value")).isEqualTo("firstNameValue");
            assertThat(Texts.camelCase("FirstName")).isEqualTo("firstName");
            assertThat(Texts.camelCase("")).isEmpty();
            assertThat(Texts.pascalCase("first name")).isEqualTo("FirstName");
            assertThat(Texts.snakeCase("firstName value-x")).isEqualTo("first_name_value_x");
            assertThat(Texts.kebabCase("firstName value_x")).isEqualTo("first-name-value-x");
        }

        @Test
        void normalizeAndCompare() {
            assertThat(Texts.normalize("A-b_C 1!")).isEqualTo("abc1");
            assertThat(Texts.normalize(null)).isEmpty();
            assertThat(Texts.equalsIgnoreCaseTrimmed(" Ab ", "aB")).isTrue();
            assertThat(Texts.equalsIgnoreCaseTrimmed(null, "")).isTrue();
            assertThat(Texts.equalsIgnoreCaseTrimmed("a", "b")).isFalse();
            assertThat(Texts.containsIgnoreCase("Hello", "ELL")).isTrue();
            assertThat(Texts.containsIgnoreCase("Hello", "x")).isFalse();
            assertThat(Texts.containsIgnoreCase(null, "a")).isFalse();
            assertThat(Texts.containsIgnoreCase("a", null)).isFalse();
        }
    }
}
