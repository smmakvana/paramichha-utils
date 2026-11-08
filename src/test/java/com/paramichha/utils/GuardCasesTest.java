package com.paramichha.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Behaviour cases for {@link Guard}: the messages and exception types each rule promises.
 * Written from the cases that used to be in GuardAndJavaTypesTest, parameterized so a new
 * rule is one more row.
 */
@DisplayName("Guard cases")
class GuardCasesTest {

    record Rejection(String name, Executable call, Class<? extends Throwable> type, String message) {}

    static Stream<Rejection> rejections() {
        return Stream.of(
                new Rejection(
                        "blank string",
                        () -> Guard.notBlank(null, "name"),
                        IllegalArgumentException.class,
                        "name must not be blank"),
                new Rejection(
                        "whitespace-only string",
                        () -> Guard.notBlank(" ", "name"),
                        IllegalArgumentException.class,
                        null),
                new Rejection(
                        "null string is empty",
                        () -> Guard.notEmpty((String) null, "name"),
                        IllegalArgumentException.class,
                        "name must not be empty"),
                new Rejection(
                        "empty string",
                        () -> Guard.notEmpty("", "name"),
                        IllegalArgumentException.class,
                        "name must not be empty"),
                new Rejection(
                        "null collection",
                        () -> Guard.notEmpty((List<?>) null, "items"),
                        IllegalArgumentException.class,
                        "items must not be empty"),
                new Rejection(
                        "empty collection",
                        () -> Guard.notEmpty(List.of(), "items"),
                        IllegalArgumentException.class,
                        "items must not be empty"),
                new Rejection(
                        "zero int is not positive",
                        () -> Guard.positive(0, "n"),
                        IllegalArgumentException.class,
                        "n must be positive, got: 0"),
                new Rejection(
                        "negative long is not positive",
                        () -> Guard.positive(-1L, "n"),
                        IllegalArgumentException.class,
                        "n must be positive, got: -1"),
                new Rejection(
                        "negative int is negative",
                        () -> Guard.notNegative(-1, "n"),
                        IllegalArgumentException.class,
                        "n must not be negative, got: -1"),
                new Rejection(
                        "int below range",
                        () -> Guard.inRange(0, 1, 5, "n"),
                        IllegalArgumentException.class,
                        "n must be between 1 and 5, got: 0"),
                new Rejection(
                        "int above range",
                        () -> Guard.inRange(6, 1, 5, "n"),
                        IllegalArgumentException.class,
                        "n must be between 1 and 5, got: 6"),
                new Rejection(
                        "long below range",
                        () -> Guard.inRange(0L, 1L, 5L, "n"),
                        IllegalArgumentException.class,
                        "n must be between 1 and 5, got: 0"),
                new Rejection(
                        "long above range",
                        () -> Guard.inRange(6L, 1L, 5L, "n"),
                        IllegalArgumentException.class,
                        "n must be between 1 and 5, got: 6"),
                new Rejection(
                        "false state", () -> Guard.state(false, "bad state"), IllegalStateException.class, "bad state"),
                new Rejection(
                        "false argument",
                        () -> Guard.argument(false, "bad arg"),
                        IllegalArgumentException.class,
                        "bad arg"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("rejections")
    @DisplayName("rejects the value with the promised exception and message")
    void rejects(Rejection rejection) {
        var thrown = assertThatThrownBy(rejection.call()::execute).isInstanceOf(rejection.type());
        if (rejection.message() != null) thrown.hasMessage(rejection.message());
    }

    @Test
    @DisplayName("trims a string that is not blank")
    void notBlankTrims() {
        assertThat(Guard.notBlank(" a ", "name")).isEqualTo("a");
    }

    @Test
    @DisplayName("keeps a whitespace-only string for notEmpty, since only length matters")
    void notEmptyKeepsWhitespace() {
        assertThat(Guard.notEmpty(" ", "name")).isEqualTo(" ");
    }

    @Test
    @DisplayName("returns the collection it was given")
    void notEmptyReturnsCollection() {
        assertThat(Guard.notEmpty(List.of(1), "items")).containsExactly(1);
    }

    @Test
    @DisplayName("accepts the boundaries of each range")
    void boundariesAccepted() {
        assertThat(Guard.notNegative(0, "n")).isZero();
        assertThat(Guard.inRange(5, 1, 5, "n")).isEqualTo(5);
        assertThat(Guard.inRange(1L, 1L, 5L, "n")).isEqualTo(1L);
    }

    @Test
    @DisplayName("passes a true state or argument")
    void trueStateAndArgumentPass() {
        Guard.state(true, "x");
        Guard.argument(true, "x");
    }
}
