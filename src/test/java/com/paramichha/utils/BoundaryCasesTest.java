package com.paramichha.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

/**
 * Boundary cross-check: every value Boundary calls valid must pass the matching DataValidation check,
 * and every value it calls invalid must fail it. The two classes are checked against each other.
 */
@DisplayName("Boundary cross-check")
class BoundaryCasesTest {

    /** One cross-check: the values from {@code values}, passed to {@code check}. */
    record Row(String name, Supplier<List<?>> values, Consumer<Object> check, boolean valid) {}

    static List<Row> rows() {
        return List.of(
                new Row(
                        "notBlank valid",
                        () -> Boundary.notBlank().valid(),
                        v -> DataValidation.notBlank((String) v),
                        true),
                new Row(
                        "notBlank invalid",
                        () -> Boundary.notBlank().invalid(),
                        v -> DataValidation.notBlank((String) v),
                        false),
                new Row(
                        "notBlank edge",
                        () -> Boundary.notBlank().edge(),
                        v -> DataValidation.notBlank((String) v),
                        true),
                new Row(
                        "notEmpty valid",
                        () -> Boundary.notEmpty().valid(),
                        v -> DataValidation.notEmpty((String) v),
                        true),
                new Row(
                        "notEmpty invalid",
                        () -> Boundary.notEmpty().invalid(),
                        v -> DataValidation.notEmpty((String) v),
                        false),
                new Row(
                        "size valid",
                        () -> Boundary.size(2, 6).valid(),
                        v -> DataValidation.size(2, 6, (String) v),
                        true),
                new Row(
                        "size invalid",
                        () -> Boundary.size(2, 6).invalid(),
                        v -> DataValidation.size(2, 6, (String) v),
                        false),
                new Row(
                        "size edge",
                        () -> Boundary.size(2, 6).edge(),
                        v -> DataValidation.size(2, 6, (String) v),
                        true),
                new Row(
                        "length valid",
                        () -> Boundary.length(1, 3).valid(),
                        v -> DataValidation.length(1, 3, (String) v),
                        true),
                new Row(
                        "size with zero minimum, valid",
                        () -> Boundary.size(0, 3).valid(),
                        v -> DataValidation.size(0, 3, (String) v),
                        true),
                new Row(
                        "size with zero minimum, invalid",
                        () -> Boundary.size(0, 3).invalid(),
                        v -> DataValidation.size(0, 3, (String) v),
                        false),
                new Row("email valid", () -> Boundary.email().valid(), v -> DataValidation.email((String) v), true),
                new Row(
                        "email invalid",
                        () -> Boundary.email().invalid(),
                        v -> DataValidation.email((String) v),
                        false),
                new Row("url valid", () -> Boundary.url().valid(), v -> DataValidation.url((String) v), true),
                new Row("url invalid", () -> Boundary.url().invalid(), v -> DataValidation.url((String) v), false),
                new Row(
                        "pattern invalid",
                        () -> Boundary.pattern("[a-z]+").invalid(),
                        v -> DataValidation.pattern("[a-z]+", (String) v),
                        false),
                new Row("min valid", () -> Boundary.min(5).validLongs(), v -> DataValidation.min(5, (Long) v), true),
                new Row(
                        "min invalid",
                        () -> Boundary.min(5).invalidLongs(),
                        v -> DataValidation.min(5, (Long) v),
                        false),
                new Row("max valid", () -> Boundary.max(5).validLongs(), v -> DataValidation.max(5, (Long) v), true),
                new Row(
                        "max invalid",
                        () -> Boundary.max(5).invalidLongs(),
                        v -> DataValidation.max(5, (Long) v),
                        false),
                new Row(
                        "range valid",
                        () -> Boundary.range(1, 9).validLongs(),
                        v -> DataValidation.max(9, DataValidation.min(1, (Long) v)),
                        true),
                new Row(
                        "range invalid",
                        () -> Boundary.range(1, 9).invalidLongs(),
                        v -> DataValidation.max(9, DataValidation.min(1, (Long) v)),
                        false),
                new Row(
                        "positive valid",
                        () -> Boundary.positive().validLongs(),
                        v -> DataValidation.positive((Long) v),
                        true),
                new Row(
                        "positive invalid",
                        () -> Boundary.positive().invalidLongs(),
                        v -> DataValidation.positive((Long) v),
                        false),
                new Row(
                        "positiveOrZero valid",
                        () -> Boundary.positiveOrZero().validLongs(),
                        v -> DataValidation.positiveOrZero((Long) v),
                        true),
                new Row(
                        "positiveOrZero invalid",
                        () -> Boundary.positiveOrZero().invalidLongs(),
                        v -> DataValidation.positiveOrZero((Long) v),
                        false),
                new Row(
                        "negative valid",
                        () -> Boundary.negative().validLongs(),
                        v -> DataValidation.negative((Long) v),
                        true),
                new Row(
                        "negative invalid",
                        () -> Boundary.negative().invalidLongs(),
                        v -> DataValidation.negative((Long) v),
                        false),
                new Row(
                        "negativeOrZero valid",
                        () -> Boundary.negativeOrZero().validLongs(),
                        v -> DataValidation.negativeOrZero((Long) v),
                        true),
                new Row(
                        "negativeOrZero invalid",
                        () -> Boundary.negativeOrZero().invalidLongs(),
                        v -> DataValidation.negativeOrZero((Long) v),
                        false),
                new Row(
                        "decimalMin valid",
                        () -> Boundary.decimalMin("1.5").valid(),
                        v -> DataValidation.decimalMin("1.5", (java.math.BigDecimal) v),
                        true),
                new Row(
                        "decimalMin invalid",
                        () -> Boundary.decimalMin("1.5").invalid(),
                        v -> DataValidation.decimalMin("1.5", (java.math.BigDecimal) v),
                        false),
                new Row(
                        "decimalMax valid",
                        () -> Boundary.decimalMax("1.5").valid(),
                        v -> DataValidation.decimalMax("1.5", (java.math.BigDecimal) v),
                        true),
                new Row(
                        "decimalMax invalid",
                        () -> Boundary.decimalMax("1.5").invalid(),
                        v -> DataValidation.decimalMax("1.5", (java.math.BigDecimal) v),
                        false),
                new Row(
                        "decimalRange valid",
                        () -> Boundary.decimalRange("1", "2").valid(),
                        v -> DataValidation.decimalMax("2", DataValidation.decimalMin("1", (java.math.BigDecimal) v)),
                        true),
                new Row(
                        "decimalRange invalid",
                        () -> Boundary.decimalRange("1", "2").invalid(),
                        v -> DataValidation.decimalMax("2", DataValidation.decimalMin("1", (java.math.BigDecimal) v)),
                        false),
                new Row(
                        "decimalPositive valid",
                        () -> Boundary.decimalPositive().valid(),
                        v -> DataValidation.positive((java.math.BigDecimal) v),
                        true),
                new Row(
                        "decimalPositive invalid",
                        () -> Boundary.decimalPositive().invalid(),
                        v -> DataValidation.positive((java.math.BigDecimal) v),
                        false),
                new Row(
                        "digits valid",
                        () -> Boundary.digits(3, 2).valid(),
                        v -> DataValidation.digits(3, 2, (java.math.BigDecimal) v),
                        true),
                new Row(
                        "digits invalid",
                        () -> Boundary.digits(3, 2).invalid(),
                        v -> DataValidation.digits(3, 2, (java.math.BigDecimal) v),
                        false),
                new Row(
                        "past dates valid",
                        () -> Boundary.past().validDates(),
                        v -> DataValidation.past((java.time.LocalDate) v),
                        true),
                new Row(
                        "past dates invalid",
                        () -> Boundary.past().invalidDates(),
                        v -> DataValidation.past((java.time.LocalDate) v),
                        false),
                new Row(
                        "past date-times valid",
                        () -> Boundary.past().validDateTimes(),
                        v -> DataValidation.past((java.time.LocalDateTime) v),
                        true),
                new Row(
                        "past date-times invalid",
                        () -> Boundary.past().invalidDateTimes(),
                        v -> DataValidation.past((java.time.LocalDateTime) v),
                        false),
                new Row(
                        "past instants valid",
                        () -> Boundary.past().validInstants(),
                        v -> DataValidation.past((java.time.Instant) v),
                        true),
                new Row(
                        "past instants invalid",
                        () -> Boundary.past().invalidInstants(),
                        v -> DataValidation.past((java.time.Instant) v),
                        false),
                new Row(
                        "pastOrPresent dates valid",
                        () -> Boundary.pastOrPresent().validDates(),
                        v -> DataValidation.pastOrPresent((java.time.LocalDate) v),
                        true),
                new Row(
                        "pastOrPresent dates invalid",
                        () -> Boundary.pastOrPresent().invalidDates(),
                        v -> DataValidation.pastOrPresent((java.time.LocalDate) v),
                        false),
                new Row(
                        "future dates valid",
                        () -> Boundary.future().validDates(),
                        v -> DataValidation.future((java.time.LocalDate) v),
                        true),
                new Row(
                        "future dates invalid",
                        () -> Boundary.future().invalidDates(),
                        v -> DataValidation.future((java.time.LocalDate) v),
                        false),
                new Row(
                        "future date-times valid",
                        () -> Boundary.future().validDateTimes(),
                        v -> DataValidation.future((java.time.LocalDateTime) v),
                        true),
                new Row(
                        "future date-times invalid",
                        () -> Boundary.future().invalidDateTimes(),
                        v -> DataValidation.future((java.time.LocalDateTime) v),
                        false),
                new Row(
                        "future instants valid",
                        () -> Boundary.future().validInstants(),
                        v -> DataValidation.future((java.time.Instant) v),
                        true),
                new Row(
                        "future instants invalid",
                        () -> Boundary.future().invalidInstants(),
                        v -> DataValidation.future((java.time.Instant) v),
                        false),
                new Row(
                        "futureOrPresent dates valid",
                        () -> Boundary.futureOrPresent().validDates(),
                        v -> DataValidation.futureOrPresent((java.time.LocalDate) v),
                        true),
                new Row(
                        "futureOrPresent dates invalid",
                        () -> Boundary.futureOrPresent().invalidDates(),
                        v -> DataValidation.futureOrPresent((java.time.LocalDate) v),
                        false),
                new Row(
                        "assertTrue valid",
                        () -> Boundary.assertTrue().valid(),
                        v -> DataValidation.assertTrue((Boolean) v),
                        true),
                new Row(
                        "assertTrue invalid",
                        () -> Boundary.assertTrue().invalid(),
                        v -> DataValidation.assertTrue((Boolean) v),
                        false),
                new Row(
                        "assertFalse valid",
                        () -> Boundary.assertFalse().valid(),
                        v -> DataValidation.assertFalse((Boolean) v),
                        true),
                new Row(
                        "assertFalse invalid",
                        () -> Boundary.assertFalse().invalid(),
                        v -> DataValidation.assertFalse((Boolean) v),
                        false));
    }

    @TestFactory
    @DisplayName("every value Boundary calls valid passes, every invalid one fails")
    Stream<DynamicTest> crossCheck() {
        return rows().stream()
                .map(row -> DynamicTest.dynamicTest(row.name(), () -> {
                    List<?> values = row.values().get();
                    assertThat(values).isNotEmpty();
                    for (Object value : values) {
                        if (row.valid()) {
                            row.check().accept(value);
                        } else {
                            assertThatThrownBy(() -> row.check().accept(value))
                                    .as("should be rejected: %s", value)
                                    .isInstanceOf(IllegalArgumentException.class);
                        }
                    }
                }));
    }

    @TestFactory
    @DisplayName("Boundary's lists cannot be changed")
    Stream<DynamicTest> listsAreUnmodifiable() {
        return Stream.of(DynamicTest.dynamicTest(
                "invalid list",
                () -> assertThatThrownBy(() -> Boundary.notBlank().invalid().add("x"))
                        .isInstanceOf(UnsupportedOperationException.class)));
    }

    @org.junit.jupiter.api.Test
    @DisplayName("one numeric boundary gives the same values for every numeric type")
    void everyNumericTypeHasTheSameValues() {
        Boundary.NumericBoundary b = Boundary.range(1, 9);
        assertThat(b.validIntegers()).containsExactly(1, 5, 9);
        assertThat(b.validDoubles()).containsExactly(1d, 5d, 9d);
        assertThat(b.validFloats()).containsExactly(1f, 5f, 9f);
        assertThat(b.validShorts()).containsExactly((short) 1, (short) 5, (short) 9);
        assertThat(b.validBytes()).containsExactly((byte) 1, (byte) 5, (byte) 9);
        assertThat(b.invalidIntegers()).containsExactly(0, 10);
        assertThat(b.invalidDoubles()).containsExactly(0d, 10d);
        assertThat(b.invalidFloats()).containsExactly(0f, 10f);
        assertThat(b.invalidShorts()).containsExactly((short) 0, (short) 10);
        assertThat(b.invalidBytes()).containsExactly((byte) 0, (byte) 10);
        assertThat(b.edgeIntegers()).containsExactly(1, 2, 8, 9);
        assertThat(b.edgeDoubles()).containsExactly(1d, 2d, 8d, 9d);
        assertThat(b.edgeFloats()).containsExactly(1f, 2f, 8f, 9f);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("min, max and range give their valid, invalid and edge values")
    void numericEdges() {
        assertThat(Boundary.min(5).edgeLongs()).containsExactly(5L, 6L);
        assertThat(Boundary.max(5).edgeLongs()).containsExactly(4L, 5L);
        assertThat(Boundary.range(1, 9).edgeLongs()).containsExactly(1L, 2L, 8L, 9L);
        assertThat(Boundary.range(1, 9).validLongs()).containsExactly(1L, 5L, 9L);
        assertThat(Boundary.positive().edgeLongs())
                .isEqualTo(Boundary.positive().validLongs());
    }

    @org.junit.jupiter.api.Test
    @DisplayName("a pattern offers no valid values it can build, and no edges")
    void patternOffersNoGeneratedValues() {
        assertThat(Boundary.pattern("[a-z]+").valid()).isEmpty();
        assertThat(Boundary.pattern("[a-z]+").edge()).isEmpty();
    }

    @org.junit.jupiter.api.Test
    @DisplayName("the boolean and email boundaries have no edge values")
    void noEdgesForBooleansAndEmail() {
        assertThat(Boundary.email().edge()).isEmpty();
        assertThat(Boundary.notEmpty().edge()).isEmpty();
        assertThat(Boundary.assertTrue().invalid()).containsExactly(null, false);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("decimal boundaries give edge values just inside each bound")
    void decimalEdges() {
        assertThat(Boundary.decimalMin("1.5").edge())
                .containsExactly(new java.math.BigDecimal("1.5"), new java.math.BigDecimal("1.51"));
        assertThat(Boundary.decimalMax("1.5").edge())
                .containsExactly(new java.math.BigDecimal("1.49"), new java.math.BigDecimal("1.5"));
        assertThat(Boundary.decimalRange("1", "2").edge()).hasSize(4);
        assertThat(Boundary.decimalPositive().edge())
                .isEqualTo(Boundary.decimalPositive().valid());
    }

    @org.junit.jupiter.api.Test
    @DisplayName("pastOrPresent invalid values are after now, futureOrPresent invalid values are before now")
    void presentEdges() {
        assertThat(Boundary.pastOrPresent().invalidDateTimes())
                .isNotEmpty()
                .allSatisfy(d -> assertThat(d).isAfter(LocalDateTime.now()));
        assertThat(Boundary.pastOrPresent().invalidInstants())
                .isNotEmpty()
                .allSatisfy(d -> assertThat(d).isAfter(Instant.now()));
        assertThat(Boundary.futureOrPresent().invalidDateTimes())
                .isNotEmpty()
                .allSatisfy(d -> assertThat(d).isBefore(LocalDateTime.now()));
        assertThat(Boundary.futureOrPresent().invalidInstants())
                .isNotEmpty()
                .allSatisfy(d -> assertThat(d).isBefore(Instant.now()));
    }

    @org.junit.jupiter.api.Test
    @DisplayName("every temporal boundary gives valid and invalid values of every temporal type")
    void everyTemporalAccessorHasValues() {
        java.util.List<Boundary.TemporalBoundary> kinds =
                List.of(Boundary.past(), Boundary.pastOrPresent(), Boundary.future(), Boundary.futureOrPresent());
        for (Boundary.TemporalBoundary b : kinds) {
            assertThat(b.validDates()).isNotEmpty();
            assertThat(b.invalidDates()).isNotEmpty();
            assertThat(b.validDateTimes()).isNotEmpty();
            assertThat(b.invalidDateTimes()).isNotEmpty();
            assertThat(b.validInstants()).isNotEmpty();
            assertThat(b.invalidInstants()).isNotEmpty();
        }
    }
}
