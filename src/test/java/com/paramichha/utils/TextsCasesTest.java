package com.paramichha.utils;

import static com.paramichha.utils.Cases.Returns;
import static com.paramichha.utils.Cases.ok;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/** Texts cases: blanks, null and empty conversions, capitalization, case styles, comparison. */
@DisplayName("Texts cases")
class TextsCasesTest {

    static Stream<Returns> returns() {
        return Stream.of(
                ok("null is blank", () -> Texts.isBlank(null), true),
                ok("tabs and spaces are blank", () -> Texts.isBlank(" \t"), true),
                ok("a letter is not blank", () -> Texts.isBlank("a"), false),
                ok("a letter is not blank (isNotBlank)", () -> Texts.isNotBlank("a"), true),
                ok("spaces are not 'not blank'", () -> Texts.isNotBlank(" "), false),
                ok("null becomes an empty string", () -> Texts.nullToEmpty(null), ""),
                ok("a value stays as it is (nullToEmpty)", () -> Texts.nullToEmpty("a"), "a"),
                ok("blank becomes null", () -> Texts.emptyToNull("  "), null),
                ok("a value stays as it is (emptyToNull)", () -> Texts.emptyToNull("a"), "a"),
                ok("null stays null (safeTrim)", () -> Texts.safeTrim(null), null),
                ok("surrounding spaces are removed", () -> Texts.safeTrim(" a "), "a"),
                ok("first letter is capitalized", () -> Texts.capitalize("abc"), "Abc"),
                ok("a space stays a space (capitalize)", () -> Texts.capitalize(" "), " "),
                ok("first letter is lowered", () -> Texts.decapitalize("Abc"), "abc"),
                ok("null stays null (decapitalize)", () -> Texts.decapitalize(null), null),
                ok("camelCase joins a word list", () -> Texts.camelCase("first_name-value"), "firstNameValue"),
                ok("camelCase keeps a camel word", () -> Texts.camelCase("FirstName"), "firstName"),
                ok("camelCase of empty is empty", () -> Texts.camelCase(""), ""),
                ok("pascalCase of spaced words", () -> Texts.pascalCase("first name"), "FirstName"),
                ok("snakeCase of mixed separators", () -> Texts.snakeCase("firstName value-x"), "first_name_value_x"),
                ok("kebabCase of mixed separators", () -> Texts.kebabCase("firstName value_x"), "first-name-value-x"),
                ok("normalize keeps letters and digits, lower-cased", () -> Texts.normalize("A-b_C 1!"), "abc1"),
                ok("normalize of null is empty", () -> Texts.normalize(null), ""),
                ok("trimmed and case-insensitive match", () -> Texts.equalsIgnoreCaseTrimmed(" Ab ", "aB"), true),
                ok("two nulls match an empty string", () -> Texts.equalsIgnoreCaseTrimmed(null, ""), true),
                ok("different words do not match", () -> Texts.equalsIgnoreCaseTrimmed("a", "b"), false),
                ok("containsIgnoreCase finds the text", () -> Texts.containsIgnoreCase("Hello", "ELL"), true),
                ok("containsIgnoreCase misses the text", () -> Texts.containsIgnoreCase("Hello", "x"), false),
                ok("null text contains nothing", () -> Texts.containsIgnoreCase(null, "a"), false),
                ok("null search matches nothing", () -> Texts.containsIgnoreCase("a", null), false));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("returns")
    @DisplayName("returns the expected value")
    void returnsExpected(Returns row) throws Throwable {
        assertThat(row.call().get()).as(row.name()).isEqualTo(row.expected());
    }

    /** Case-style conversions must agree for every common input shape. */
    @ParameterizedTest(name = "{0}")
    @MethodSource("sameWords")
    @DisplayName("every case style converts the same words the same way")
    void caseStylesAgree(String input) {
        assertThat(Texts.camelCase(input)).as(input).isEqualTo("helloWorld");
        assertThat(Texts.pascalCase(input)).as(input).isEqualTo("HelloWorld");
        assertThat(Texts.snakeCase(input)).as(input).isEqualTo("hello_world");
        assertThat(Texts.kebabCase(input)).as(input).isEqualTo("hello-world");
    }

    static List<String> sameWords() {
        return List.of("hello world", "hello_world", "hello-world", "HelloWorld");
    }
}
