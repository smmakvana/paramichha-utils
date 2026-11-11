package com.paramichha.utils;

import static com.paramichha.utils.Cases.Rejects;
import static com.paramichha.utils.Cases.Returns;
import static com.paramichha.utils.Cases.no;
import static com.paramichha.utils.Cases.ok;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/** Tokens cases: {@code {name}} placeholders filled from a map or from key-value pairs. */
@DisplayName("Tokens cases")
class TokensCasesTest {

    static Stream<Returns> returns() {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("n", 3);
        values.put("x", null);
        return Stream.of(
                ok("a null template comes back as null", () -> Tokens.resolve(null, Map.of("a", 1)), null),
                ok(
                        "a null map leaves the template as it is",
                        () -> Tokens.resolve("{a}", (Map<String, ?>) null),
                        "{a}"),
                ok(
                        "values are written with String.valueOf, a null value as null, a missing key stays",
                        () -> Tokens.resolve("{n}-{x}-{y}", values),
                        "3-null-{y}"),
                ok("a null template with pairs comes back as null", () -> Tokens.resolve(null, "a", "b"), null),
                ok("no pairs leaves the template as it is", () -> Tokens.resolve("{a}"), "{a}"),
                ok("a null array leaves the template as it is", () -> Tokens.resolve("{a}", (String[]) null), "{a}"),
                ok(
                        "a null value is written as null, the last one wins",
                        () -> Tokens.resolve("{a}{b}", "a", "1", "b", null),
                        "1"),
                ok(
                        "the resolved name and value fill the template",
                        () -> Tokens.resolve("Hi {name}, {n}!", "name", "Ada", "n", "3"),
                        "Hi Ada, 3!"),
                ok(
                        "an unknown token stays in the result",
                        () -> Tokens.resolve("Hi {name} {missing}", Map.of("name", "Ada")),
                        "Hi Ada {missing}"));
    }

    static Stream<Rejects> rejects() {
        return Stream.of(no(
                "a key without a value",
                () -> Tokens.resolve("{a}", "a"),
                "keyValues must be alternating key-value pairs, got 1 args"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("returns")
    @DisplayName("resolves the template as described")
    void resolves(Returns row) throws Throwable {
        assertThat(row.call().get()).as(row.name()).isEqualTo(row.expected());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("rejects")
    @DisplayName("rejects the call with the stated message")
    void rejectsWithMessage(Rejects row) {
        assertThatThrownBy(row.call()::get).isInstanceOf(row.type()).hasMessage(row.message());
    }
}
