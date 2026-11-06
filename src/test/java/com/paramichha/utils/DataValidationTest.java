package com.paramichha.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingSupplier;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DataValidationTest {

    /** The check rejects the value with exactly this message. */
    private static void rejects(String message, ThrowingSupplier<?> check) {
        assertThatThrownBy(check::get).isInstanceOf(IllegalArgumentException.class).hasMessage(message);
    }

    private static final BigDecimal MINUS_ONE = BigDecimal.ONE.negate();

    @Nested
    @DisplayName("null checks")
    class Nulls {
        @Test
        void notNullAndIsNull() {
            assertThat(DataValidation.notNull("a")).isEqualTo("a");
            rejects("value must not be null", () -> DataValidation.notNull(null));
            rejects("custom", () -> DataValidation.notNull(null, "custom"));
            assertThat(DataValidation.<String>isNull(null)).isNull();
            rejects("value must be null", () -> DataValidation.isNull("a"));
            rejects("custom", () -> DataValidation.isNull("a", "custom"));
        }
    }

    @Nested
    @DisplayName("strings")
    class Strings {
        @Test
        void notBlankAndNotEmpty() {
            assertThat(DataValidation.notBlank(" a ")).isEqualTo(" a ");
            rejects("value must not be blank", () -> DataValidation.notBlank(null));
            rejects("value must not be blank", () -> DataValidation.notBlank(" "));
            assertThat(DataValidation.notEmpty(" ")).isEqualTo(" ");
            rejects("value must not be empty", () -> DataValidation.notEmpty((String) null));
            rejects("value must not be empty", () -> DataValidation.notEmpty(""));
        }

        @Test
        void sizeAndLength() {
            assertThat(DataValidation.size(1, 3, "ab")).isEqualTo("ab");
            rejects("value length must be between 1 and 3", () -> DataValidation.size(1, 3, (String) null));
            rejects("value length must be between 1 and 3", () -> DataValidation.size(1, 3, ""));
            rejects("value length must be between 1 and 3", () -> DataValidation.size(1, 3, "abcd"));
            assertThat(DataValidation.length(1, 3, "abc")).isEqualTo("abc");
            assertThat(DataValidation.length(1, 3, "ab", "too long")).isEqualTo("ab");
            rejects("too long", () -> DataValidation.length(1, 3, "abcd", "too long"));
        }

        @Test
        void collectionSizeAndNotEmpty() {
            assertThat(DataValidation.size(1, 2, List.of(1))).containsExactly(1);
            rejects("collection size must be between 1 and 2", () -> DataValidation.size(1, 2, (List<?>) null));
            rejects("collection size must be between 1 and 2", () -> DataValidation.size(1, 2, List.of()));
            rejects("collection size must be between 1 and 2", () -> DataValidation.size(1, 2, List.of(1, 2, 3)));
            assertThat(DataValidation.notEmpty(List.of(1))).containsExactly(1);
            rejects("collection must not be empty", () -> DataValidation.notEmpty((List<?>) null));
            rejects("collection must not be empty", () -> DataValidation.notEmpty(List.of()));
        }

        @Test
        void formats() {
            assertThat(DataValidation.email("a.b@example.co.uk")).isEqualTo("a.b@example.co.uk");
            rejects("value must be a valid email address", () -> DataValidation.email(null));
            rejects("value must be a valid email address", () -> DataValidation.email("a@b"));
            assertThat(DataValidation.pattern("[a-z]+", "abc")).isEqualTo("abc");
            rejects("value must match pattern: [a-z]+", () -> DataValidation.pattern("[a-z]+", null));
            rejects("value must match pattern: [a-z]+", () -> DataValidation.pattern("[a-z]+", "ABC"));
            assertThat(DataValidation.url("https://example.com/x")).isEqualTo("https://example.com/x");
            rejects("value must be a valid URL", () -> DataValidation.url(null));
            rejects("value must be a valid URL", () -> DataValidation.url("example.com"));
            assertThat(DataValidation.isbn("978-0-306-40615-7")).isEqualTo("978-0-306-40615-7");
            assertThat(DataValidation.isbn("0306406152")).isEqualTo("0306406152");
            rejects("value must be a valid ISBN", () -> DataValidation.isbn(null));
            rejects("value must be a valid ISBN", () -> DataValidation.isbn("12-34"));
            assertThat(DataValidation.ean("12345678")).isEqualTo("12345678");
            assertThat(DataValidation.ean("1234567890123")).isEqualTo("1234567890123");
            rejects("value must be a valid EAN barcode", () -> DataValidation.ean(null));
            rejects("value must be a valid EAN barcode", () -> DataValidation.ean("123"));
        }

        @Test
        void creditCardUsesTheLuhnCheck() {
            assertThat(DataValidation.creditCard("4111 1111-1111 1111")).isEqualTo("4111 1111-1111 1111");
            assertThat(DataValidation.creditCard("79927398713")).isEqualTo("79927398713");
            rejects("value must be a valid credit card number", () -> DataValidation.creditCard(null));
            rejects("value must be a valid credit card number", () -> DataValidation.creditCard("4111111111111112"));
            rejects("value must be a valid credit card number", () -> DataValidation.creditCard("4111x"));
        }
    }

    @Nested
    @DisplayName("min and max, every numeric type")
    class MinMax {
        @Test
        void min() {
            assertThat(DataValidation.min(2, 2)).isEqualTo(2);
            rejects("value must be >= 2", () -> DataValidation.min(2, 1));
            assertThat(DataValidation.min(2, 2L)).isEqualTo(2L);
            rejects("value must be >= 2", () -> DataValidation.min(2, 1L));
            assertThat(DataValidation.min(2, 2.5d)).isEqualTo(2.5d);
            rejects("value must be >= 2", () -> DataValidation.min(2, 1.5d));
            assertThat(DataValidation.min(2, 2.5f)).isEqualTo(2.5f);
            rejects("value must be >= 2", () -> DataValidation.min(2, 1.5f));
            assertThat(DataValidation.min(2, (short) 2)).isEqualTo((short) 2);
            rejects("value must be >= 2", () -> DataValidation.min(2, (short) 1));
            assertThat(DataValidation.min(2, (byte) 2)).isEqualTo((byte) 2);
            rejects("value must be >= 2", () -> DataValidation.min(2, (byte) 1));
            assertThat(DataValidation.min(2, new BigDecimal("2.0"))).isEqualByComparingTo("2");
            rejects("value must be >= 2", () -> DataValidation.min(2, BigDecimal.ONE));
            rejects("value must be >= 2", () -> DataValidation.min(2, (BigDecimal) null));
        }

        @Test
        void max() {
            assertThat(DataValidation.max(2, 2)).isEqualTo(2);
            rejects("value must be <= 2", () -> DataValidation.max(2, 3));
            assertThat(DataValidation.max(2, 2L)).isEqualTo(2L);
            rejects("value must be <= 2", () -> DataValidation.max(2, 3L));
            assertThat(DataValidation.max(2, 1.5d)).isEqualTo(1.5d);
            rejects("value must be <= 2", () -> DataValidation.max(2, 2.5d));
            assertThat(DataValidation.max(2, 1.5f)).isEqualTo(1.5f);
            rejects("value must be <= 2", () -> DataValidation.max(2, 2.5f));
            assertThat(DataValidation.max(2, (short) 2)).isEqualTo((short) 2);
            rejects("value must be <= 2", () -> DataValidation.max(2, (short) 3));
            assertThat(DataValidation.max(2, (byte) 2)).isEqualTo((byte) 2);
            rejects("value must be <= 2", () -> DataValidation.max(2, (byte) 3));
            assertThat(DataValidation.max(2, BigDecimal.ONE)).isEqualByComparingTo("1");
            rejects("value must be <= 2", () -> DataValidation.max(2, BigDecimal.TEN));
            rejects("value must be <= 2", () -> DataValidation.max(2, (BigDecimal) null));
        }
    }

    @Nested
    @DisplayName("signs, every numeric type")
    class Signs {
        @Test
        void positive() {
            assertThat(DataValidation.positive(1)).isEqualTo(1);
            rejects("value must be positive", () -> DataValidation.positive(0));
            assertThat(DataValidation.positive(1L)).isEqualTo(1L);
            rejects("value must be positive", () -> DataValidation.positive(0L));
            assertThat(DataValidation.positive(0.5d)).isEqualTo(0.5d);
            rejects("value must be positive", () -> DataValidation.positive(0d));
            assertThat(DataValidation.positive(0.5f)).isEqualTo(0.5f);
            rejects("value must be positive", () -> DataValidation.positive(0f));
            assertThat(DataValidation.positive(BigDecimal.ONE)).isEqualByComparingTo("1");
            rejects("value must be positive", () -> DataValidation.positive(BigDecimal.ZERO));
            rejects("value must be positive", () -> DataValidation.positive((BigDecimal) null));
        }

        @Test
        void positiveOrZero() {
            assertThat(DataValidation.positiveOrZero(0)).isZero();
            rejects("value must be >= 0", () -> DataValidation.positiveOrZero(-1));
            assertThat(DataValidation.positiveOrZero(0L)).isZero();
            rejects("value must be >= 0", () -> DataValidation.positiveOrZero(-1L));
            assertThat(DataValidation.positiveOrZero(0d)).isZero();
            rejects("value must be >= 0", () -> DataValidation.positiveOrZero(-0.5d));
            assertThat(DataValidation.positiveOrZero(0f)).isZero();
            rejects("value must be >= 0", () -> DataValidation.positiveOrZero(-0.5f));
            assertThat(DataValidation.positiveOrZero(BigDecimal.ZERO)).isEqualByComparingTo("0");
            rejects("value must be >= 0", () -> DataValidation.positiveOrZero(MINUS_ONE));
            rejects("value must be >= 0", () -> DataValidation.positiveOrZero((BigDecimal) null));
        }

        @Test
        void negative() {
            assertThat(DataValidation.negative(-1)).isEqualTo(-1);
            rejects("value must be negative", () -> DataValidation.negative(0));
            assertThat(DataValidation.negative(-1L)).isEqualTo(-1L);
            rejects("value must be negative", () -> DataValidation.negative(0L));
            assertThat(DataValidation.negative(-0.5d)).isEqualTo(-0.5d);
            rejects("value must be negative", () -> DataValidation.negative(0d));
            assertThat(DataValidation.negative(-0.5f)).isEqualTo(-0.5f);
            rejects("value must be negative", () -> DataValidation.negative(0f));
            assertThat(DataValidation.negative(MINUS_ONE)).isEqualByComparingTo("-1");
            rejects("value must be negative", () -> DataValidation.negative(BigDecimal.ZERO));
            rejects("value must be negative", () -> DataValidation.negative((BigDecimal) null));
        }

        @Test
        void negativeOrZero() {
            assertThat(DataValidation.negativeOrZero(0)).isZero();
            rejects("value must be <= 0", () -> DataValidation.negativeOrZero(1));
            assertThat(DataValidation.negativeOrZero(0L)).isZero();
            rejects("value must be <= 0", () -> DataValidation.negativeOrZero(1L));
            assertThat(DataValidation.negativeOrZero(0d)).isZero();
            rejects("value must be <= 0", () -> DataValidation.negativeOrZero(0.5d));
            assertThat(DataValidation.negativeOrZero(0f)).isZero();
            rejects("value must be <= 0", () -> DataValidation.negativeOrZero(0.5f));
            assertThat(DataValidation.negativeOrZero(BigDecimal.ZERO)).isEqualByComparingTo("0");
            rejects("value must be <= 0", () -> DataValidation.negativeOrZero(BigDecimal.ONE));
            rejects("value must be <= 0", () -> DataValidation.negativeOrZero((BigDecimal) null));
        }
    }

    @Nested
    @DisplayName("decimals")
    class Decimals {
        @Test
        void decimalMinAndMax() {
            assertThat(DataValidation.decimalMin("1.5", new BigDecimal("1.5"))).isEqualByComparingTo("1.5");
            rejects("value must be >= 1.5", () -> DataValidation.decimalMin("1.5", new BigDecimal("1.49")));
            rejects("value must be >= 1.5", () -> DataValidation.decimalMin("1.5", null));
            assertThat(DataValidation.decimalMax("1.5", new BigDecimal("1.5"))).isEqualByComparingTo("1.5");
            rejects("value must be <= 1.5", () -> DataValidation.decimalMax("1.5", new BigDecimal("1.51")));
            rejects("value must be <= 1.5", () -> DataValidation.decimalMax("1.5", null));
        }

        @Test
        void digits() {
            String message = "value must have at most 3 integer and 2 fraction digits";
            assertThat(DataValidation.digits(3, 2, new BigDecimal("-123.45"))).isEqualByComparingTo("-123.45");
            assertThat(DataValidation.digits(3, 2, new BigDecimal("0.5"))).isEqualByComparingTo("0.5");
            assertThat(DataValidation.digits(3, 2, new BigDecimal("12"))).isEqualByComparingTo("12");
            rejects(message, () -> DataValidation.digits(3, 2, null));
            rejects(message, () -> DataValidation.digits(3, 2, new BigDecimal("1234")));
            rejects(message, () -> DataValidation.digits(3, 2, new BigDecimal("1.234")));
        }
    }

    @Nested
    @DisplayName("dates and times")
    class Temporal {
        private final LocalDate today = LocalDate.now();
        private final LocalDateTime anHourAgo = LocalDateTime.now().minusHours(1);
        private final LocalDateTime inAnHour = LocalDateTime.now().plusHours(1);
        private final Instant before = Instant.now().minusSeconds(3600);
        private final Instant after = Instant.now().plusSeconds(3600);

        @Test
        void past() {
            String m = "value must be in the past";
            assertThat(DataValidation.past(today.minusDays(1))).isEqualTo(today.minusDays(1));
            rejects(m, () -> DataValidation.past(today));
            rejects(m, () -> DataValidation.past((LocalDate) null));
            assertThat(DataValidation.past(anHourAgo)).isEqualTo(anHourAgo);
            rejects(m, () -> DataValidation.past(inAnHour));
            rejects(m, () -> DataValidation.past((LocalDateTime) null));
            assertThat(DataValidation.past(before)).isEqualTo(before);
            rejects(m, () -> DataValidation.past(after));
            rejects(m, () -> DataValidation.past((Instant) null));
        }

        @Test
        void pastOrPresent() {
            String m = "value must be in the past or present";
            assertThat(DataValidation.pastOrPresent(today)).isEqualTo(today);
            rejects(m, () -> DataValidation.pastOrPresent(today.plusDays(1)));
            rejects(m, () -> DataValidation.pastOrPresent((LocalDate) null));
            assertThat(DataValidation.pastOrPresent(anHourAgo)).isEqualTo(anHourAgo);
            rejects(m, () -> DataValidation.pastOrPresent(inAnHour));
            rejects(m, () -> DataValidation.pastOrPresent((LocalDateTime) null));
            assertThat(DataValidation.pastOrPresent(before)).isEqualTo(before);
            rejects(m, () -> DataValidation.pastOrPresent(after));
            rejects(m, () -> DataValidation.pastOrPresent((Instant) null));
        }

        @Test
        void future() {
            String m = "value must be in the future";
            assertThat(DataValidation.future(today.plusDays(1))).isEqualTo(today.plusDays(1));
            rejects(m, () -> DataValidation.future(today));
            rejects(m, () -> DataValidation.future((LocalDate) null));
            assertThat(DataValidation.future(inAnHour)).isEqualTo(inAnHour);
            rejects(m, () -> DataValidation.future(anHourAgo));
            rejects(m, () -> DataValidation.future((LocalDateTime) null));
            assertThat(DataValidation.future(after)).isEqualTo(after);
            rejects(m, () -> DataValidation.future(before));
            rejects(m, () -> DataValidation.future((Instant) null));
        }

        @Test
        void futureOrPresent() {
            String m = "value must be in the future or present";
            assertThat(DataValidation.futureOrPresent(today)).isEqualTo(today);
            rejects(m, () -> DataValidation.futureOrPresent(today.minusDays(1)));
            rejects(m, () -> DataValidation.futureOrPresent((LocalDate) null));
            assertThat(DataValidation.futureOrPresent(inAnHour)).isEqualTo(inAnHour);
            rejects(m, () -> DataValidation.futureOrPresent(anHourAgo));
            rejects(m, () -> DataValidation.futureOrPresent((LocalDateTime) null));
            assertThat(DataValidation.futureOrPresent(after)).isEqualTo(after);
            rejects(m, () -> DataValidation.futureOrPresent(before));
            rejects(m, () -> DataValidation.futureOrPresent((Instant) null));
        }
    }

    @Nested
    @DisplayName("booleans")
    class Booleans {
        @Test
        void assertTrueAndFalse() {
            assertThat(DataValidation.assertTrue(true)).isTrue();
            rejects("value must be true", () -> DataValidation.assertTrue(false));
            assertThat(DataValidation.assertTrue(Boolean.TRUE)).isTrue();
            rejects("value must be true", () -> DataValidation.assertTrue(Boolean.FALSE));
            rejects("value must be true", () -> DataValidation.assertTrue((Boolean) null));
            assertThat(DataValidation.assertFalse(false)).isFalse();
            rejects("value must be false", () -> DataValidation.assertFalse(true));
            assertThat(DataValidation.assertFalse(Boolean.FALSE)).isFalse();
            rejects("value must be false", () -> DataValidation.assertFalse(Boolean.TRUE));
            rejects("value must be false", () -> DataValidation.assertFalse((Boolean) null));
        }
    }
}
