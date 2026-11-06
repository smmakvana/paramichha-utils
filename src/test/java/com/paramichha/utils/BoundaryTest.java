package com.paramichha.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Every value Boundary calls valid must pass the matching DataValidation check, and every value it
 * calls invalid must fail it. The two classes are checked against each other.
 */
class BoundaryTest {

    private static <T> void allPass(List<T> values, Consumer<T> check) {
        assertThat(values).isNotEmpty();
        values.forEach(check);
    }

    private static <T> void allFail(List<T> values, Consumer<T> check) {
        assertThat(values).isNotEmpty();
        for (T value : values) {
            assertThatThrownBy(() -> check.accept(value)).as("should be rejected: %s", value)
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("strings")
    class Strings {
        @Test
        void notBlank() {
            allPass(Boundary.notBlank().valid(), DataValidation::notBlank);
            allFail(Boundary.notBlank().invalid(), DataValidation::notBlank);
            assertThat(Boundary.notBlank().invalid()).startsWith((String) null);
            allPass(Boundary.notBlank().edge(), DataValidation::notBlank);
        }

        @Test
        void notEmpty() {
            allPass(Boundary.notEmpty().valid(), DataValidation::notEmpty);
            allFail(Boundary.notEmpty().invalid(), DataValidation::notEmpty);
            assertThat(Boundary.notEmpty().edge()).isEmpty();
        }

        @Test
        void size() {
            allPass(Boundary.size(2, 6).valid(), v -> DataValidation.size(2, 6, v));
            allFail(Boundary.size(2, 6).invalid(), v -> DataValidation.size(2, 6, v));
            allPass(Boundary.size(2, 6).edge(), v -> DataValidation.size(2, 6, v));
            allPass(Boundary.length(1, 3).valid(), v -> DataValidation.length(1, 3, v));
        }

        @Test
        void sizeWithZeroMinimumDoesNotCallTheEmptyStringInvalid() {
            List<String> invalid = Boundary.size(0, 3).invalid();
            assertThat(invalid).doesNotContain("");
            allFail(invalid, v -> DataValidation.size(0, 3, v));
            allPass(Boundary.size(0, 3).valid(), v -> DataValidation.size(0, 3, v));
        }

        @Test
        void emailAndUrl() {
            allPass(Boundary.email().valid(), DataValidation::email);
            allFail(Boundary.email().invalid(), DataValidation::email);
            assertThat(Boundary.email().edge()).isEmpty();
            allPass(Boundary.url().valid(), DataValidation::url);
            allFail(Boundary.url().invalid(), DataValidation::url);
        }

        @Test
        void patternOffersNoValidValuesAndRejectsNullAndEmpty() {
            assertThat(Boundary.pattern("[a-z]+").valid()).isEmpty();
            allFail(Boundary.pattern("[a-z]+").invalid(), v -> DataValidation.pattern("[a-z]+", v));
        }

        @Test
        void listsAreUnmodifiable() {
            assertThatThrownBy(() -> Boundary.notBlank().invalid().add("x"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    @DisplayName("numbers")
    class Numbers {
        @Test
        void minMaxRange() {
            allPass(Boundary.min(5).validLongs(), v -> DataValidation.min(5, v));
            allFail(Boundary.min(5).invalidLongs(), v -> DataValidation.min(5, v));
            assertThat(Boundary.min(5).edgeLongs()).containsExactly(5L, 6L);
            allPass(Boundary.max(5).validLongs(), v -> DataValidation.max(5, v));
            allFail(Boundary.max(5).invalidLongs(), v -> DataValidation.max(5, v));
            assertThat(Boundary.max(5).edgeLongs()).containsExactly(4L, 5L);
            allPass(Boundary.range(1, 9).validLongs(), v -> DataValidation.max(9, DataValidation.min(1, v)));
            allFail(Boundary.range(1, 9).invalidLongs(), v -> DataValidation.max(9, DataValidation.min(1, v)));
            assertThat(Boundary.range(1, 9).edgeLongs()).containsExactly(1L, 2L, 8L, 9L);
            assertThat(Boundary.range(1, 9).validLongs()).containsExactly(1L, 5L, 9L);
        }

        @Test
        void signs() {
            allPass(Boundary.positive().validLongs(), v -> DataValidation.positive(v));
            allFail(Boundary.positive().invalidLongs(), v -> DataValidation.positive(v));
            allPass(Boundary.positiveOrZero().validLongs(), v -> DataValidation.positiveOrZero(v));
            allFail(Boundary.positiveOrZero().invalidLongs(), v -> DataValidation.positiveOrZero(v));
            allPass(Boundary.negative().validLongs(), v -> DataValidation.negative(v));
            allFail(Boundary.negative().invalidLongs(), v -> DataValidation.negative(v));
            allPass(Boundary.negativeOrZero().validLongs(), v -> DataValidation.negativeOrZero(v));
            allFail(Boundary.negativeOrZero().invalidLongs(), v -> DataValidation.negativeOrZero(v));
            assertThat(Boundary.positive().edgeLongs()).isEqualTo(Boundary.positive().validLongs());
        }

        @Test
        void everyNumericTypeIsTheSameValues() {
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
    }

    @Nested
    @DisplayName("decimals")
    class Decimals {
        @Test
        void minMaxRangePositive() {
            allPass(Boundary.decimalMin("1.5").valid(), v -> DataValidation.decimalMin("1.5", v));
            allFail(Boundary.decimalMin("1.5").invalid(), v -> DataValidation.decimalMin("1.5", v));
            allPass(Boundary.decimalMin("1.5").edge(), v -> DataValidation.decimalMin("1.5", v));
            allPass(Boundary.decimalMax("1.5").valid(), v -> DataValidation.decimalMax("1.5", v));
            allFail(Boundary.decimalMax("1.5").invalid(), v -> DataValidation.decimalMax("1.5", v));
            allPass(Boundary.decimalMax("1.5").edge(), v -> DataValidation.decimalMax("1.5", v));
            Consumer<BigDecimal> inRange = v -> DataValidation.decimalMax("2", DataValidation.decimalMin("1", v));
            allPass(Boundary.decimalRange("1", "2").valid(), inRange);
            allFail(Boundary.decimalRange("1", "2").invalid(), inRange);
            allPass(Boundary.decimalRange("1", "2").edge(), inRange);
            allPass(Boundary.decimalPositive().valid(), DataValidation::positive);
            allFail(Boundary.decimalPositive().invalid(), DataValidation::positive);
            assertThat(Boundary.decimalPositive().edge()).isEqualTo(Boundary.decimalPositive().valid());
        }

        @Test
        void digits() {
            allPass(Boundary.digits(3, 2).valid(), v -> DataValidation.digits(3, 2, v));
            allFail(Boundary.digits(3, 2).invalid(), v -> DataValidation.digits(3, 2, v));
        }
    }

    @Nested
    @DisplayName("dates and times")
    class Temporal {
        @Test
        void past() {
            allPass(Boundary.past().validDates(), v -> DataValidation.past(v));
            allFail(Boundary.past().invalidDates(), v -> DataValidation.past(v));
            allPass(Boundary.past().validDateTimes(), v -> DataValidation.past(v));
            allFail(Boundary.past().invalidDateTimes(), v -> DataValidation.past(v));
            allPass(Boundary.past().validInstants(), v -> DataValidation.past(v));
            allFail(Boundary.past().invalidInstants(), v -> DataValidation.past(v));
        }

        @Test
        void pastOrPresent() {
            allPass(Boundary.pastOrPresent().validDates(), v -> DataValidation.pastOrPresent(v));
            allFail(Boundary.pastOrPresent().invalidDates(), v -> DataValidation.pastOrPresent(v));
            allPass(Boundary.pastOrPresent().validDateTimes(), v -> DataValidation.pastOrPresent(v));
            allFail(Boundary.pastOrPresent().invalidDateTimes(), v -> DataValidation.pastOrPresent(v));
            allPass(Boundary.pastOrPresent().validInstants(), v -> DataValidation.pastOrPresent(v));
            allFail(Boundary.pastOrPresent().invalidInstants(), v -> DataValidation.pastOrPresent(v));
        }

        @Test
        void future() {
            // Values that are only seconds ahead are checked straight away, before they expire.
            allPass(Boundary.future().validDates(), v -> DataValidation.future(v));
            allFail(Boundary.future().invalidDates(), v -> DataValidation.future(v));
            allPass(Boundary.future().validDateTimes(), v -> DataValidation.future(v));
            allFail(Boundary.future().invalidDateTimes(), v -> DataValidation.future(v));
            allPass(Boundary.future().validInstants(), v -> DataValidation.future(v));
            allFail(Boundary.future().invalidInstants(), v -> DataValidation.future(v));
        }

        @Test
        void futureOrPresent() {
            allPass(Boundary.futureOrPresent().validDates(), v -> DataValidation.futureOrPresent(v));
            allFail(Boundary.futureOrPresent().invalidDates(), v -> DataValidation.futureOrPresent(v));
            allPass(Boundary.futureOrPresent().validDateTimes(), v -> DataValidation.futureOrPresent(v));
            allFail(Boundary.futureOrPresent().invalidDateTimes(), v -> DataValidation.futureOrPresent(v));
            allPass(Boundary.futureOrPresent().validInstants(), v -> DataValidation.futureOrPresent(v));
            allFail(Boundary.futureOrPresent().invalidInstants(), v -> DataValidation.futureOrPresent(v));
        }

        @Test
        void typesAreRight() {
            assertThat(Boundary.past().validDates()).allSatisfy(d -> assertThat(d).isInstanceOf(LocalDate.class));
            assertThat(Boundary.past().validDateTimes()).allSatisfy(d -> assertThat(d).isInstanceOf(LocalDateTime.class));
            assertThat(Boundary.past().validInstants()).allSatisfy(d -> assertThat(d).isInstanceOf(Instant.class));
        }
    }

    @Nested
    @DisplayName("booleans")
    class Booleans {
        @Test
        void assertTrueAndFalse() {
            allPass(Boundary.assertTrue().valid(), (Boolean v) -> DataValidation.assertTrue(v));
            allFail(Boundary.assertTrue().invalid(), (Boolean v) -> DataValidation.assertTrue(v));
            allPass(Boundary.assertFalse().valid(), (Boolean v) -> DataValidation.assertFalse(v));
            allFail(Boundary.assertFalse().invalid(), (Boolean v) -> DataValidation.assertFalse(v));
            assertThat(Boundary.assertTrue().invalid()).containsExactly(null, false);
        }
    }
}
