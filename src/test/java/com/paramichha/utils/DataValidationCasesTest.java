package com.paramichha.utils;

import static com.paramichha.utils.Cases.Rejects;
import static com.paramichha.utils.Cases.Returns;
import static com.paramichha.utils.Cases.no;
import static com.paramichha.utils.Cases.ok;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * DataValidation cases: every check returns its value when it passes, and throws
 * IllegalArgumentException with the stated message when it fails.
 */
@DisplayName("DataValidation cases")
class DataValidationCasesTest {

    private static final BigDecimal MINUS_ONE = BigDecimal.ONE.negate();
    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDateTime AN_HOUR_AGO = LocalDateTime.now().minusHours(1);
    private static final LocalDateTime IN_AN_HOUR = LocalDateTime.now().plusHours(1);
    private static final Instant BEFORE = Instant.now().minusSeconds(3600);
    private static final Instant AFTER = Instant.now().plusSeconds(3600);

    static Stream<Returns> returns() {
        return Stream.of(
                // null checks
                ok("notNull keeps a value", () -> DataValidation.notNull("a"), "a"),
                ok("isNull keeps null", () -> DataValidation.<String>isNull(null), null),

                // strings
                ok("notBlank keeps a value", () -> DataValidation.notBlank(" a "), " a "),
                ok("notEmpty keeps a space", () -> DataValidation.notEmpty(" "), " "),
                ok("size keeps a string of the right length", () -> DataValidation.size(1, 3, "ab"), "ab"),
                ok("length keeps a string of the right length", () -> DataValidation.length(1, 3, "abc"), "abc"),
                ok(
                        "length with a message keeps a valid string",
                        () -> DataValidation.length(1, 3, "ab", "too long"),
                        "ab"),
                ok(
                        "size keeps a collection of the right size",
                        () -> DataValidation.size(1, 2, List.of(1)),
                        List.of(1)),
                ok("notEmpty keeps a collection with items", () -> DataValidation.notEmpty(List.of(1)), List.of(1)),
                ok("a plain email address", () -> DataValidation.email("a.b@example.co.uk"), "a.b@example.co.uk"),
                ok("a pattern match", () -> DataValidation.pattern("[a-z]+", "abc"), "abc"),
                ok("a URL", () -> DataValidation.url("https://example.com/x"), "https://example.com/x"),
                ok("an ISBN-13 with hyphens", () -> DataValidation.isbn("978-0-306-40615-7"), "978-0-306-40615-7"),
                ok("an ISBN-10", () -> DataValidation.isbn("0306406152"), "0306406152"),
                ok("an 8-digit EAN", () -> DataValidation.ean("12345678"), "12345678"),
                ok("a 13-digit EAN", () -> DataValidation.ean("1234567890123"), "1234567890123"),
                ok(
                        "a card number with spaces and hyphens (Luhn)",
                        () -> DataValidation.creditCard("4111 1111-1111 1111"),
                        "4111 1111-1111 1111"),
                ok(
                        "a short card number that passes Luhn",
                        () -> DataValidation.creditCard("79927398713"),
                        "79927398713"),

                // min and max, every numeric type
                ok("min int at the bound", () -> DataValidation.min(2, 2), 2),
                ok("min long at the bound", () -> DataValidation.min(2, 2L), 2L),
                ok("min double above the bound", () -> DataValidation.min(2, 2.5d), 2.5d),
                ok("min float above the bound", () -> DataValidation.min(2, 2.5f), 2.5f),
                ok("min short at the bound", () -> DataValidation.min(2, (short) 2), (short) 2),
                ok("min byte at the bound", () -> DataValidation.min(2, (byte) 2), (byte) 2),
                ok("max int at the bound", () -> DataValidation.max(2, 2), 2),
                ok("max long at the bound", () -> DataValidation.max(2, 2L), 2L),
                ok("max double below the bound", () -> DataValidation.max(2, 1.5d), 1.5d),
                ok("max float below the bound", () -> DataValidation.max(2, 1.5f), 1.5f),
                ok("max short at the bound", () -> DataValidation.max(2, (short) 2), (short) 2),
                ok("max byte at the bound", () -> DataValidation.max(2, (byte) 2), (byte) 2),

                // signs
                ok("positive int", () -> DataValidation.positive(1), 1),
                ok("positive long", () -> DataValidation.positive(1L), 1L),
                ok("positive double", () -> DataValidation.positive(0.5d), 0.5d),
                ok("positive float", () -> DataValidation.positive(0.5f), 0.5f),
                ok("positive zero-or-more int at zero", () -> DataValidation.positiveOrZero(0), 0),
                ok("positiveOrZero long at zero", () -> DataValidation.positiveOrZero(0L), 0L),
                ok("positiveOrZero double at zero", () -> DataValidation.positiveOrZero(0d), 0d),
                ok("positiveOrZero float at zero", () -> DataValidation.positiveOrZero(0f), 0f),
                ok("negative int", () -> DataValidation.negative(-1), -1),
                ok("negative long", () -> DataValidation.negative(-1L), -1L),
                ok("negative double", () -> DataValidation.negative(-0.5d), -0.5d),
                ok("negative float", () -> DataValidation.negative(-0.5f), -0.5f),
                ok("negativeOrZero int at zero", () -> DataValidation.negativeOrZero(0), 0),
                ok("negativeOrZero long at zero", () -> DataValidation.negativeOrZero(0L), 0L),
                ok("negativeOrZero double at zero", () -> DataValidation.negativeOrZero(0d), 0d),
                ok("negativeOrZero float at zero", () -> DataValidation.negativeOrZero(0f), 0f),

                // decimals
                ok(
                        "decimalMin at the bound",
                        () -> DataValidation.decimalMin("1.5", new BigDecimal("1.5")),
                        new BigDecimal("1.5")),
                ok(
                        "decimalMax at the bound",
                        () -> DataValidation.decimalMax("1.5", new BigDecimal("1.5")),
                        new BigDecimal("1.5")),
                ok(
                        "digits with a negative value",
                        () -> DataValidation.digits(3, 2, new BigDecimal("-123.45")),
                        new BigDecimal("-123.45")),
                ok(
                        "digits with a fraction",
                        () -> DataValidation.digits(3, 2, new BigDecimal("0.5")),
                        new BigDecimal("0.5")),
                ok(
                        "digits with no fraction",
                        () -> DataValidation.digits(3, 2, new BigDecimal("12")),
                        new BigDecimal("12")),

                // dates and times
                ok("past date", () -> DataValidation.past(TODAY.minusDays(1)), TODAY.minusDays(1)),
                ok("past date-time", () -> DataValidation.past(AN_HOUR_AGO), AN_HOUR_AGO),
                ok("past instant", () -> DataValidation.past(BEFORE), BEFORE),
                ok("past-or-present date today", () -> DataValidation.pastOrPresent(TODAY), TODAY),
                ok(
                        "past-or-present date-time an hour ago",
                        () -> DataValidation.pastOrPresent(AN_HOUR_AGO),
                        AN_HOUR_AGO),
                ok("past-or-present instant before now", () -> DataValidation.pastOrPresent(BEFORE), BEFORE),
                ok("future date", () -> DataValidation.future(TODAY.plusDays(1)), TODAY.plusDays(1)),
                ok("future date-time", () -> DataValidation.future(IN_AN_HOUR), IN_AN_HOUR),
                ok("future instant", () -> DataValidation.future(AFTER), AFTER),
                ok("future-or-present date today", () -> DataValidation.futureOrPresent(TODAY), TODAY),
                ok(
                        "future-or-present date-time in an hour",
                        () -> DataValidation.futureOrPresent(IN_AN_HOUR),
                        IN_AN_HOUR),
                ok("future-or-present instant after now", () -> DataValidation.futureOrPresent(AFTER), AFTER),

                // booleans
                ok("assertTrue passes true", () -> DataValidation.assertTrue(true), true),
                ok("assertTrue passes Boolean.TRUE", () -> DataValidation.assertTrue(Boolean.TRUE), true),
                ok("assertFalse passes false", () -> DataValidation.assertFalse(false), false),
                ok("assertFalse passes Boolean.FALSE", () -> DataValidation.assertFalse(Boolean.FALSE), false));
    }

    static Stream<Rejects> rejects() {
        return Stream.of(
                // null checks
                no("notNull rejects null", () -> DataValidation.notNull(null), "value must not be null"),
                no("notNull with a message", () -> DataValidation.notNull(null, "custom"), "custom"),
                no("isNull rejects a value", () -> DataValidation.isNull("a"), "value must be null"),
                no("isNull with a message", () -> DataValidation.isNull("a", "custom"), "custom"),

                // strings
                no("notBlank rejects null", () -> DataValidation.notBlank(null), "value must not be blank"),
                no("notBlank rejects spaces", () -> DataValidation.notBlank(" "), "value must not be blank"),
                no("notEmpty rejects null", () -> DataValidation.notEmpty((String) null), "value must not be empty"),
                no("notEmpty rejects an empty string", () -> DataValidation.notEmpty(""), "value must not be empty"),
                no(
                        "size rejects null",
                        () -> DataValidation.size(1, 3, (String) null),
                        "value length must be between 1 and 3"),
                no(
                        "size rejects an empty string",
                        () -> DataValidation.size(1, 3, ""),
                        "value length must be between 1 and 3"),
                no(
                        "size rejects a long string",
                        () -> DataValidation.size(1, 3, "abcd"),
                        "value length must be between 1 and 3"),
                no(
                        "length with a message rejects a long string",
                        () -> DataValidation.length(1, 3, "abcd", "too long"),
                        "too long"),
                no(
                        "collection size rejects null",
                        () -> DataValidation.size(1, 2, (List<?>) null),
                        "collection size must be between 1 and 2"),
                no(
                        "collection size rejects an empty list",
                        () -> DataValidation.size(1, 2, List.of()),
                        "collection size must be between 1 and 2"),
                no(
                        "collection size rejects too many items",
                        () -> DataValidation.size(1, 2, List.of(1, 2, 3)),
                        "collection size must be between 1 and 2"),
                no(
                        "notEmpty rejects null list",
                        () -> DataValidation.notEmpty((List<?>) null),
                        "collection must not be empty"),
                no(
                        "notEmpty rejects an empty list",
                        () -> DataValidation.notEmpty(List.of()),
                        "collection must not be empty"),
                no("email rejects null", () -> DataValidation.email(null), "value must be a valid email address"),
                no(
                        "email rejects a bare host",
                        () -> DataValidation.email("a@b"),
                        "value must be a valid email address"),
                no(
                        "pattern rejects null",
                        () -> DataValidation.pattern("[a-z]+", null),
                        "value must match pattern: [a-z]+"),
                no(
                        "pattern rejects uppercase",
                        () -> DataValidation.pattern("[a-z]+", "ABC"),
                        "value must match pattern: [a-z]+"),
                no("url rejects null", () -> DataValidation.url(null), "value must be a valid URL"),
                no(
                        "url rejects a value without a scheme",
                        () -> DataValidation.url("example.com"),
                        "value must be a valid URL"),
                no("isbn rejects null", () -> DataValidation.isbn(null), "value must be a valid ISBN"),
                no("isbn rejects a short value", () -> DataValidation.isbn("12-34"), "value must be a valid ISBN"),
                no("ean rejects null", () -> DataValidation.ean(null), "value must be a valid EAN barcode"),
                no("ean rejects a short value", () -> DataValidation.ean("123"), "value must be a valid EAN barcode"),
                no(
                        "credit card rejects null",
                        () -> DataValidation.creditCard(null),
                        "value must be a valid credit card number"),
                no(
                        "credit card rejects a failed Luhn check",
                        () -> DataValidation.creditCard("4111111111111112"),
                        "value must be a valid credit card number"),
                no(
                        "credit card rejects letters",
                        () -> DataValidation.creditCard("4111x"),
                        "value must be a valid credit card number"),

                // min and max
                no("min int below the bound", () -> DataValidation.min(2, 1), "value must be >= 2"),
                no("min long below the bound", () -> DataValidation.min(2, 1L), "value must be >= 2"),
                no("min double below the bound", () -> DataValidation.min(2, 1.5d), "value must be >= 2"),
                no("min float below the bound", () -> DataValidation.min(2, 1.5f), "value must be >= 2"),
                no("min short below the bound", () -> DataValidation.min(2, (short) 1), "value must be >= 2"),
                no("min byte below the bound", () -> DataValidation.min(2, (byte) 1), "value must be >= 2"),
                no("min BigDecimal below the bound", () -> DataValidation.min(2, BigDecimal.ONE), "value must be >= 2"),
                no("min rejects null BigDecimal", () -> DataValidation.min(2, (BigDecimal) null), "value must be >= 2"),
                no("max int above the bound", () -> DataValidation.max(2, 3), "value must be <= 2"),
                no("max long above the bound", () -> DataValidation.max(2, 3L), "value must be <= 2"),
                no("max double above the bound", () -> DataValidation.max(2, 2.5d), "value must be <= 2"),
                no("max float above the bound", () -> DataValidation.max(2, 2.5f), "value must be <= 2"),
                no("max short above the bound", () -> DataValidation.max(2, (short) 3), "value must be <= 2"),
                no("max byte above the bound", () -> DataValidation.max(2, (byte) 3), "value must be <= 2"),
                no("max BigDecimal above the bound", () -> DataValidation.max(2, BigDecimal.TEN), "value must be <= 2"),
                no("max rejects null BigDecimal", () -> DataValidation.max(2, (BigDecimal) null), "value must be <= 2"),

                // signs
                no("positive rejects zero", () -> DataValidation.positive(0), "value must be positive"),
                no("positive rejects zero long", () -> DataValidation.positive(0L), "value must be positive"),
                no("positive rejects zero double", () -> DataValidation.positive(0d), "value must be positive"),
                no("positive rejects zero float", () -> DataValidation.positive(0f), "value must be positive"),
                no(
                        "positive rejects zero BigDecimal",
                        () -> DataValidation.positive(BigDecimal.ZERO),
                        "value must be positive"),
                no(
                        "positive rejects null BigDecimal",
                        () -> DataValidation.positive((BigDecimal) null),
                        "value must be positive"),
                no("positiveOrZero rejects -1", () -> DataValidation.positiveOrZero(-1), "value must be >= 0"),
                no("positiveOrZero rejects -1 long", () -> DataValidation.positiveOrZero(-1L), "value must be >= 0"),
                no(
                        "positiveOrZero rejects -0.5 double",
                        () -> DataValidation.positiveOrZero(-0.5d),
                        "value must be >= 0"),
                no(
                        "positiveOrZero rejects -0.5 float",
                        () -> DataValidation.positiveOrZero(-0.5f),
                        "value must be >= 0"),
                no(
                        "positiveOrZero rejects -1 BigDecimal",
                        () -> DataValidation.positiveOrZero(MINUS_ONE),
                        "value must be >= 0"),
                no(
                        "positiveOrZero rejects null BigDecimal",
                        () -> DataValidation.positiveOrZero((BigDecimal) null),
                        "value must be >= 0"),
                no("negative rejects zero", () -> DataValidation.negative(0), "value must be negative"),
                no("negative rejects zero long", () -> DataValidation.negative(0L), "value must be negative"),
                no("negative rejects zero double", () -> DataValidation.negative(0d), "value must be negative"),
                no("negative rejects zero float", () -> DataValidation.negative(0f), "value must be negative"),
                no(
                        "negative rejects zero BigDecimal",
                        () -> DataValidation.negative(BigDecimal.ZERO),
                        "value must be negative"),
                no(
                        "negative rejects null BigDecimal",
                        () -> DataValidation.negative((BigDecimal) null),
                        "value must be negative"),
                no("negativeOrZero rejects 1", () -> DataValidation.negativeOrZero(1), "value must be <= 0"),
                no("negativeOrZero rejects 1 long", () -> DataValidation.negativeOrZero(1L), "value must be <= 0"),
                no(
                        "negativeOrZero rejects 0.5 double",
                        () -> DataValidation.negativeOrZero(0.5d),
                        "value must be <= 0"),
                no("negativeOrZero rejects 0.5 float", () -> DataValidation.negativeOrZero(0.5f), "value must be <= 0"),
                no(
                        "negativeOrZero rejects 1 BigDecimal",
                        () -> DataValidation.negativeOrZero(BigDecimal.ONE),
                        "value must be <= 0"),
                no(
                        "negativeOrZero rejects null BigDecimal",
                        () -> DataValidation.negativeOrZero((BigDecimal) null),
                        "value must be <= 0"),

                // decimals
                no(
                        "decimalMin rejects a value below the bound",
                        () -> DataValidation.decimalMin("1.5", new BigDecimal("1.49")),
                        "value must be >= 1.5"),
                no("decimalMin rejects null", () -> DataValidation.decimalMin("1.5", null), "value must be >= 1.5"),
                no(
                        "decimalMax rejects a value above the bound",
                        () -> DataValidation.decimalMax("1.5", new BigDecimal("1.51")),
                        "value must be <= 1.5"),
                no("decimalMax rejects null", () -> DataValidation.decimalMax("1.5", null), "value must be <= 1.5"),
                no(
                        "digits rejects null",
                        () -> DataValidation.digits(3, 2, null),
                        "value must have at most 3 integer and 2 fraction digits"),
                no(
                        "digits rejects too many integer digits",
                        () -> DataValidation.digits(3, 2, new BigDecimal("1234")),
                        "value must have at most 3 integer and 2 fraction digits"),
                no(
                        "digits rejects too many fraction digits",
                        () -> DataValidation.digits(3, 2, new BigDecimal("1.234")),
                        "value must have at most 3 integer and 2 fraction digits"),

                // dates and times
                no("past rejects today", () -> DataValidation.past(TODAY), "value must be in the past"),
                no("past rejects null date", () -> DataValidation.past((LocalDate) null), "value must be in the past"),
                no(
                        "past rejects a future date-time",
                        () -> DataValidation.past(IN_AN_HOUR),
                        "value must be in the past"),
                no(
                        "past rejects null date-time",
                        () -> DataValidation.past((LocalDateTime) null),
                        "value must be in the past"),
                no("past rejects a future instant", () -> DataValidation.past(AFTER), "value must be in the past"),
                no("past rejects null instant", () -> DataValidation.past((Instant) null), "value must be in the past"),
                no(
                        "past-or-present rejects tomorrow",
                        () -> DataValidation.pastOrPresent(TODAY.plusDays(1)),
                        "value must be in the past or present"),
                no(
                        "past-or-present rejects null date",
                        () -> DataValidation.pastOrPresent((LocalDate) null),
                        "value must be in the past or present"),
                no(
                        "past-or-present rejects a future date-time",
                        () -> DataValidation.pastOrPresent(IN_AN_HOUR),
                        "value must be in the past or present"),
                no(
                        "past-or-present rejects null date-time",
                        () -> DataValidation.pastOrPresent((LocalDateTime) null),
                        "value must be in the past or present"),
                no(
                        "past-or-present rejects a future instant",
                        () -> DataValidation.pastOrPresent(AFTER),
                        "value must be in the past or present"),
                no(
                        "past-or-present rejects null instant",
                        () -> DataValidation.pastOrPresent((Instant) null),
                        "value must be in the past or present"),
                no("future rejects today", () -> DataValidation.future(TODAY), "value must be in the future"),
                no(
                        "future rejects null date",
                        () -> DataValidation.future((LocalDate) null),
                        "value must be in the future"),
                no(
                        "future rejects a past date-time",
                        () -> DataValidation.future(AN_HOUR_AGO),
                        "value must be in the future"),
                no(
                        "future rejects null date-time",
                        () -> DataValidation.future((LocalDateTime) null),
                        "value must be in the future"),
                no("future rejects a past instant", () -> DataValidation.future(BEFORE), "value must be in the future"),
                no(
                        "future rejects null instant",
                        () -> DataValidation.future((Instant) null),
                        "value must be in the future"),
                no(
                        "future-or-present rejects yesterday",
                        () -> DataValidation.futureOrPresent(TODAY.minusDays(1)),
                        "value must be in the future or present"),
                no(
                        "future-or-present rejects null date",
                        () -> DataValidation.futureOrPresent((LocalDate) null),
                        "value must be in the future or present"),
                no(
                        "future-or-present rejects a past date-time",
                        () -> DataValidation.futureOrPresent(AN_HOUR_AGO),
                        "value must be in the future or present"),
                no(
                        "future-or-present rejects null date-time",
                        () -> DataValidation.futureOrPresent((LocalDateTime) null),
                        "value must be in the future or present"),
                no(
                        "future-or-present rejects a past instant",
                        () -> DataValidation.futureOrPresent(BEFORE),
                        "value must be in the future or present"),
                no(
                        "future-or-present rejects null instant",
                        () -> DataValidation.futureOrPresent((Instant) null),
                        "value must be in the future or present"),

                // booleans
                no("assertTrue rejects false", () -> DataValidation.assertTrue(false), "value must be true"),
                no(
                        "assertTrue rejects Boolean.FALSE",
                        () -> DataValidation.assertTrue(Boolean.FALSE),
                        "value must be true"),
                no("assertTrue rejects null", () -> DataValidation.assertTrue((Boolean) null), "value must be true"),
                no("assertFalse rejects true", () -> DataValidation.assertFalse(true), "value must be false"),
                no(
                        "assertFalse rejects Boolean.TRUE",
                        () -> DataValidation.assertFalse(Boolean.TRUE),
                        "value must be false"),
                no(
                        "assertFalse rejects null",
                        () -> DataValidation.assertFalse((Boolean) null),
                        "value must be false"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("returns")
    @DisplayName("passes and returns the value")
    void returnsTheValue(Returns row) throws Throwable {
        assertThat(row.call().get()).as(row.name()).isEqualTo(row.expected());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("rejects")
    @DisplayName("rejects with the stated exception and message")
    void rejectsWithMessage(Rejects row) {
        assertThatThrownBy(row.call()::get).isInstanceOf(row.type()).hasMessage(row.message());
    }
}
