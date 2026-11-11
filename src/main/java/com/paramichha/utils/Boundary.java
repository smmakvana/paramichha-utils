package com.paramichha.utils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Generates boundary values for Jakarta constraint annotations.
 *
 * <p>Uses {@link DataValidation} internally — same predicate, consistent behaviour.
 *
 * <p>Three value categories:
 * <ul>
 *   <li>{@code valid()}   — values that pass the constraint</li>
 *   <li>{@code invalid()} — values that fail the constraint</li>
 *   <li>{@code edge()}    — values right at the boundary</li>
 * </ul>
 *
 * <pre>
 * // String
 * Boundary.notBlank().valid()              // ["a", "hello", "hello world"]
 * Boundary.notBlank().invalid()            // [null, "", " ", "\t"]
 * Boundary.size(8, 100).valid()            // ["abcdefgh", "validvalue"]
 * Boundary.size(8, 100).invalid()          // [null, "short", "a".repeat(101)]
 *
 * // Numeric — type resolved at terminal call
 * Boundary.min(1).valid(Integer.class)     // [1, 50, Integer.MAX_VALUE]
 * Boundary.min(1).invalid(Integer.class)   // [0, -1, Integer.MIN_VALUE]
 * Boundary.range(1, 100).edge(Integer.class) // [1, 2, 99, 100]
 *
 * // Decimal
 * Boundary.positive().valid()              // [0.01, 1.00, 999999.99]
 * Boundary.positive().invalid()            // [null, 0.00, -0.01]
 *
 * // Temporal
 * Boundary.past().valid(LocalDate.class)   // [yesterday, lastYear]
 * Boundary.past().invalid(LocalDate.class) // [today, tomorrow]
 * </pre>
 */
public final class Boundary {

    private Boundary() {}

    // =========================================================================
    // String boundaries
    // =========================================================================

    public static StringBoundary notBlank() {
        return new StringBoundary(StringConstraint.NOT_BLANK, 0, 0);
    }

    public static StringBoundary notEmpty() {
        return new StringBoundary(StringConstraint.NOT_EMPTY, 0, 0);
    }

    public static StringBoundary size(int min, int max) {
        return new StringBoundary(StringConstraint.SIZE, min, max);
    }

    public static StringBoundary length(int min, int max) {
        return size(min, max);
    }

    public static StringBoundary email() {
        return new StringBoundary(StringConstraint.EMAIL, 0, 0);
    }

    public static StringBoundary url() {
        return new StringBoundary(StringConstraint.URL, 0, 0);
    }

    public static StringBoundary pattern(String regexp) {
        return new StringBoundary(StringConstraint.PATTERN, 0, 0);
    }

    public static final class StringBoundary {
        private final StringConstraint type;
        private final int min;
        private final int max;

        private StringBoundary(StringConstraint type, int min, int max) {
            this.type = type;
            this.min = min;
            this.max = max;
        }

        /**
         * Values that satisfy this constraint.
         */
        public List<String> valid() {
            return switch (type) {
                case NOT_BLANK -> List.of("a", "hello", "hello world", " hello ");
                case NOT_EMPTY -> List.of("a", " ", "hello");
                case SIZE -> List.of("a".repeat(min), "a".repeat(Math.max(min, (min + max) / 2)), "a".repeat(max));
                case EMAIL -> List.of("user@example.com", "test.user+tag@domain.co.uk");
                case URL -> List.of("https://example.com", "http://test.org/path?q=1");
                case PATTERN -> List.of(); // cannot generate without knowing the regexp semantics
            };
        }

        /**
         * Values that break this constraint.
         */
        public List<String> invalid() {
            return switch (type) {
                case NOT_BLANK -> withNull(List.of("", " ", "   ", "\t", "\n"));
                case NOT_EMPTY -> withNull(List.of(""));
                case SIZE ->
                    min > 0
                            ? withNull(List.of("a".repeat(min - 1), "a".repeat(max + 1)))
                            : withNull(List.of("a".repeat(max + 1)));
                case EMAIL -> withNull(List.of("", "notanemail", "@nodomain", "no@", "a@b"));
                case URL -> withNull(List.of("", "notaurl", "ftp://", "://missing"));
                case PATTERN -> withNull(List.of(""));
            };
        }

        /**
         * Values on, or right next to, the limits of this constraint.
         */
        public List<String> edge() {
            return switch (type) {
                case SIZE -> List.of("a".repeat(min), "a".repeat(min + 1), "a".repeat(max - 1), "a".repeat(max));
                case NOT_BLANK -> List.of("a", " a", "a ");
                default -> List.of();
            };
        }
    }

    // =========================================================================
    // Numeric boundaries — int, long, double, float, short, byte
    // =========================================================================

    public static NumericBoundary min(long min) {
        return new NumericBoundary(min, Long.MAX_VALUE, NumericConstraint.MIN);
    }

    public static NumericBoundary max(long max) {
        return new NumericBoundary(Long.MIN_VALUE, max, NumericConstraint.MAX);
    }

    public static NumericBoundary range(long min, long max) {
        return new NumericBoundary(min, max, NumericConstraint.RANGE);
    }

    public static NumericBoundary positive() {
        return new NumericBoundary(1, Long.MAX_VALUE, NumericConstraint.POSITIVE);
    }

    public static NumericBoundary positiveOrZero() {
        return new NumericBoundary(0, Long.MAX_VALUE, NumericConstraint.POSITIVE_OR_ZERO);
    }

    public static NumericBoundary negative() {
        return new NumericBoundary(Long.MIN_VALUE, -1, NumericConstraint.NEGATIVE);
    }

    public static NumericBoundary negativeOrZero() {
        return new NumericBoundary(Long.MIN_VALUE, 0, NumericConstraint.NEGATIVE_OR_ZERO);
    }

    public static final class NumericBoundary {
        private final long min;
        private final long max;
        private final NumericConstraint numType;

        private NumericBoundary(long min, long max, NumericConstraint numType) {
            this.min = min;
            this.max = max;
            this.numType = numType;
        }

        // ── valid ─────────────────────────────────────────────────────────
        public List<Integer> validIntegers() {
            return validLongs().stream().map(Long::intValue).toList();
        }

        public List<Long> validLongs() {
            return computeValid();
        }

        public List<Double> validDoubles() {
            return validLongs().stream().map(Long::doubleValue).toList();
        }

        public List<Float> validFloats() {
            return validLongs().stream().map(Long::floatValue).toList();
        }

        public List<Short> validShorts() {
            return validLongs().stream().map(Long::shortValue).toList();
        }

        public List<Byte> validBytes() {
            return validLongs().stream().map(Long::byteValue).toList();
        }

        // ── invalid ───────────────────────────────────────────────────────
        public List<Integer> invalidIntegers() {
            return invalidLongs().stream().map(Long::intValue).toList();
        }

        public List<Long> invalidLongs() {
            return computeInvalid();
        }

        public List<Double> invalidDoubles() {
            return invalidLongs().stream().map(Long::doubleValue).toList();
        }

        public List<Float> invalidFloats() {
            return invalidLongs().stream().map(Long::floatValue).toList();
        }

        public List<Short> invalidShorts() {
            return invalidLongs().stream().map(Long::shortValue).toList();
        }

        public List<Byte> invalidBytes() {
            return invalidLongs().stream().map(Long::byteValue).toList();
        }

        // ── edge ──────────────────────────────────────────────────────────
        public List<Integer> edgeIntegers() {
            return edgeLongs().stream().map(Long::intValue).toList();
        }

        public List<Long> edgeLongs() {
            return computeEdge();
        }

        public List<Double> edgeDoubles() {
            return edgeLongs().stream().map(Long::doubleValue).toList();
        }

        public List<Float> edgeFloats() {
            return edgeLongs().stream().map(Long::floatValue).toList();
        }

        private List<Long> computeValid() {
            long mid = min == Long.MIN_VALUE || max == Long.MAX_VALUE ? Math.max(min, 0) : (min + max) / 2;
            return switch (numType) {
                case MIN -> List.of(min, mid, Long.MAX_VALUE / 2);
                case MAX -> List.of(Long.MIN_VALUE / 2, mid, max);
                case RANGE -> List.of(min, mid, max);
                case POSITIVE -> List.of(1L, 100L, Long.MAX_VALUE / 2);
                case POSITIVE_OR_ZERO -> List.of(0L, 1L, 100L);
                case NEGATIVE -> List.of(-1L, -100L, Long.MIN_VALUE / 2);
                case NEGATIVE_OR_ZERO -> List.of(0L, -1L, -100L);
            };
        }

        private List<Long> computeInvalid() {
            return switch (numType) {
                case MIN -> List.of(min - 1, 0L, Long.MIN_VALUE / 2);
                case MAX -> List.of(max + 1, Long.MAX_VALUE / 2);
                case RANGE -> List.of(min - 1, max + 1);
                case POSITIVE -> List.of(0L, -1L, Long.MIN_VALUE / 2);
                case POSITIVE_OR_ZERO -> List.of(-1L, Long.MIN_VALUE / 2);
                case NEGATIVE -> List.of(0L, 1L, Long.MAX_VALUE / 2);
                case NEGATIVE_OR_ZERO -> List.of(1L, Long.MAX_VALUE / 2);
            };
        }

        private List<Long> computeEdge() {
            return switch (numType) {
                case MIN -> List.of(min, min + 1);
                case MAX -> List.of(max - 1, max);
                case RANGE -> List.of(min, min + 1, max - 1, max);
                default -> validLongs();
            };
        }
    }

    // =========================================================================
    // Decimal boundaries — BigDecimal
    // =========================================================================

    public static DecimalBoundary decimalMin(String min) {
        return new DecimalBoundary(new BigDecimal(min), null, DecimalConstraint.MIN);
    }

    public static DecimalBoundary decimalMax(String max) {
        return new DecimalBoundary(null, new BigDecimal(max), DecimalConstraint.MAX);
    }

    public static DecimalBoundary decimalRange(String min, String max) {
        return new DecimalBoundary(new BigDecimal(min), new BigDecimal(max), DecimalConstraint.RANGE);
    }

    public static DecimalBoundary decimalPositive() {
        return new DecimalBoundary(BigDecimal.ZERO, null, DecimalConstraint.POSITIVE);
    }

    public static DecimalBoundary digits(int integer, int fraction) {
        return new DecimalBoundary(integer, fraction);
    }

    public static final class DecimalBoundary {
        private final BigDecimal min;
        private final BigDecimal max;
        private final DecimalConstraint decType;
        private final int integerDigits;
        private final int fractionDigits;

        private DecimalBoundary(BigDecimal min, BigDecimal max, DecimalConstraint decType) {
            this.min = min;
            this.max = max;
            this.decType = decType;
            this.integerDigits = 0;
            this.fractionDigits = 0;
        }

        private DecimalBoundary(int integerDigits, int fractionDigits) {
            this.min = null;
            this.max = null;
            this.decType = DecimalConstraint.DIGITS;
            this.integerDigits = integerDigits;
            this.fractionDigits = fractionDigits;
        }

        /**
         * Values that satisfy this constraint.
         */
        public List<BigDecimal> valid() {
            return switch (decType) {
                case MIN -> List.of(min, min.add(BigDecimal.ONE), min.add(new BigDecimal("100")));
                case MAX -> List.of(max.subtract(new BigDecimal("100")), max.subtract(BigDecimal.ONE), max);
                case RANGE -> List.of(min, min.add(max.subtract(min).divide(new BigDecimal("2"))), max);
                case POSITIVE -> List.of(new BigDecimal("0.01"), BigDecimal.ONE, new BigDecimal("999.99"));
                case DIGITS ->
                    List.of(
                            new BigDecimal("1." + "0".repeat(fractionDigits)),
                            new BigDecimal("9".repeat(integerDigits) + "." + "9".repeat(fractionDigits)));
            };
        }

        /**
         * Values that break this constraint.
         */
        public List<BigDecimal> invalid() {
            return switch (decType) {
                case MIN -> withNull(List.of(min.subtract(new BigDecimal("0.01")), min.subtract(BigDecimal.ONE)));
                case MAX -> withNull(List.of(max.add(new BigDecimal("0.01")), max.add(BigDecimal.ONE)));
                case RANGE -> withNull(List.of(min.subtract(new BigDecimal("0.01")), max.add(new BigDecimal("0.01"))));
                case POSITIVE -> withNull(List.of(BigDecimal.ZERO, new BigDecimal("-0.01")));
                case DIGITS ->
                    withNull(List.of(
                            new BigDecimal("9".repeat(integerDigits + 1)),
                            new BigDecimal("0." + "9".repeat(fractionDigits + 1))));
            };
        }

        /**
         * Values on, or right next to, the limits of this constraint.
         */
        public List<BigDecimal> edge() {
            return switch (decType) {
                case MIN -> List.of(min, min.add(new BigDecimal("0.01")));
                case MAX -> List.of(max.subtract(new BigDecimal("0.01")), max);
                case RANGE -> List.of(min, min.add(new BigDecimal("0.01")), max.subtract(new BigDecimal("0.01")), max);
                default -> valid();
            };
        }
    }

    // =========================================================================
    // Temporal boundaries
    // =========================================================================

    public static TemporalBoundary past() {
        return new TemporalBoundary(TemporalConstraint.PAST);
    }

    public static TemporalBoundary pastOrPresent() {
        return new TemporalBoundary(TemporalConstraint.PAST_OR_PRESENT);
    }

    public static TemporalBoundary future() {
        return new TemporalBoundary(TemporalConstraint.FUTURE);
    }

    public static TemporalBoundary futureOrPresent() {
        return new TemporalBoundary(TemporalConstraint.FUTURE_OR_PRESENT);
    }

    public static final class TemporalBoundary {
        private final TemporalConstraint tempType;

        private TemporalBoundary(TemporalConstraint tempType) {
            this.tempType = tempType;
        }

        // ── LocalDate ─────────────────────────────────────────────────────
        /**
         * Dates that satisfy this constraint, relative to today.
         */
        public List<LocalDate> validDates() {
            return switch (tempType) {
                case PAST ->
                    List.of(LocalDate.now().minusDays(1), LocalDate.now().minusYears(1));
                case PAST_OR_PRESENT -> List.of(LocalDate.now().minusDays(1), LocalDate.now());
                case FUTURE ->
                    List.of(LocalDate.now().plusDays(1), LocalDate.now().plusYears(1));
                case FUTURE_OR_PRESENT ->
                    List.of(LocalDate.now(), LocalDate.now().plusDays(1));
            };
        }

        /**
         * Dates that break this constraint, relative to today.
         */
        public List<LocalDate> invalidDates() {
            return switch (tempType) {
                case PAST -> List.of(LocalDate.now(), LocalDate.now().plusDays(1));
                case PAST_OR_PRESENT -> List.of(LocalDate.now().plusDays(1));
                case FUTURE -> List.of(LocalDate.now(), LocalDate.now().minusDays(1));
                case FUTURE_OR_PRESENT -> List.of(LocalDate.now().minusDays(1));
            };
        }

        // ── LocalDateTime ─────────────────────────────────────────────────
        /**
         * Date-times that satisfy this constraint, relative to now.
         */
        public List<LocalDateTime> validDateTimes() {
            return switch (tempType) {
                case PAST ->
                    List.of(
                            LocalDateTime.now().minusSeconds(1),
                            LocalDateTime.now().minusYears(1));
                case PAST_OR_PRESENT ->
                    List.of(
                            LocalDateTime.now().minusSeconds(1),
                            LocalDateTime.now().minusMinutes(1));
                case FUTURE ->
                    List.of(
                            LocalDateTime.now().plusSeconds(1),
                            LocalDateTime.now().plusYears(1));
                case FUTURE_OR_PRESENT ->
                    List.of(
                            LocalDateTime.now().plusSeconds(1),
                            LocalDateTime.now().plusMinutes(1));
            };
        }

        /**
         * Date-times that break this constraint, relative to now.
         */
        public List<LocalDateTime> invalidDateTimes() {
            return switch (tempType) {
                case PAST -> List.of(LocalDateTime.now().plusSeconds(1));
                case PAST_OR_PRESENT -> List.of(LocalDateTime.now().plusSeconds(1));
                case FUTURE -> List.of(LocalDateTime.now().minusSeconds(1));
                case FUTURE_OR_PRESENT -> List.of(LocalDateTime.now().minusSeconds(1));
            };
        }

        // ── Instant ───────────────────────────────────────────────────────
        /**
         * Instants that satisfy this constraint, relative to now.
         */
        public List<Instant> validInstants() {
            return switch (tempType) {
                case PAST ->
                    List.of(Instant.now().minusSeconds(1), Instant.now().minusSeconds(3600));
                case PAST_OR_PRESENT ->
                    List.of(Instant.now().minusSeconds(1), Instant.now().minusSeconds(60));
                case FUTURE ->
                    List.of(Instant.now().plusSeconds(1), Instant.now().plusSeconds(3600));
                case FUTURE_OR_PRESENT ->
                    List.of(Instant.now().plusSeconds(1), Instant.now().plusSeconds(60));
            };
        }

        /**
         * Instants that break this constraint, relative to now.
         */
        public List<Instant> invalidInstants() {
            return switch (tempType) {
                case PAST -> List.of(Instant.now().plusSeconds(1));
                case PAST_OR_PRESENT -> List.of(Instant.now().plusSeconds(1));
                case FUTURE -> List.of(Instant.now().minusSeconds(1));
                case FUTURE_OR_PRESENT -> List.of(Instant.now().minusSeconds(1));
            };
        }
    }

    // =========================================================================
    // Boolean boundaries
    // =========================================================================

    public static BooleanBoundary assertTrue() {
        return new BooleanBoundary(true);
    }

    public static BooleanBoundary assertFalse() {
        return new BooleanBoundary(false);
    }

    public static final class BooleanBoundary {
        private final boolean expected;

        private BooleanBoundary(boolean expected) {
            this.expected = expected;
        }

        /**
         * Values that satisfy this constraint.
         */
        public List<Boolean> valid() {
            return List.of(expected);
        }

        /**
         * Values that break this constraint.
         */
        public List<Boolean> invalid() {
            return withNull(List.of(!expected));
        }
    }

    /**
     * {@code null} followed by the given values, as an unmodifiable list. {@code List.of} rejects
     * null elements, and null is the first invalid value for every constraint here.
     */
    private static <T> List<T> withNull(List<T> values) {
        List<T> list = new ArrayList<>(values.size() + 1);
        list.add(null);
        list.addAll(values);
        return Collections.unmodifiableList(list);
    }

    // =========================================================================
    // Internal enums
    // =========================================================================

    private enum StringConstraint {
        NOT_BLANK,
        NOT_EMPTY,
        SIZE,
        EMAIL,
        URL,
        PATTERN
    }

    private enum NumericConstraint {
        MIN,
        MAX,
        RANGE,
        POSITIVE,
        POSITIVE_OR_ZERO,
        NEGATIVE,
        NEGATIVE_OR_ZERO
    }

    private enum DecimalConstraint {
        MIN,
        MAX,
        RANGE,
        POSITIVE,
        DIGITS
    }

    private enum TemporalConstraint {
        PAST,
        PAST_OR_PRESENT,
        FUTURE,
        FUTURE_OR_PRESENT
    }
}
