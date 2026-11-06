package com.paramichha.utils;

import java.math.BigDecimal;
import java.time.*;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * Fluent validation utility mirroring Jakarta constraint annotations.
 *
 * <p>Each method validates a single value and returns it if valid,
 * throws {@link IllegalArgumentException} if invalid.
 *
 * <p>Two overloads per method:
 * <pre>
 * DataValidation.min(1, value)                    // default message
 * DataValidation.min(1, value, "custom message")  // custom message
 * </pre>
 *
 * <p>Return value allows inline use:
 * <pre>
 * this.quantity = DataValidation.min(1, quantity);
 * this.email    = DataValidation.email(email);
 * </pre>
 */
public final class DataValidation {

    private DataValidation() {}

    // =========================================================================
    // Null checks — @NotNull @Null
    // =========================================================================

    public static <T> T notNull(T value) {
        return notNull(value, "value must not be null");
    }

    public static <T> T notNull(T value, String message) {
        if (value == null) throw new IllegalArgumentException(message);
        return value;
    }

    public static <T> T isNull(T value) {
        return isNull(value, "value must be null");
    }

    public static <T> T isNull(T value, String message) {
        if (value != null) throw new IllegalArgumentException(message);
        return null;
    }

    // =========================================================================
    // String — @NotBlank @NotEmpty @Size @Email @Pattern @URL @Length
    //          @CreditCardNumber @ISBN @EAN
    // =========================================================================

    public static String notBlank(String value) {
        return notBlank(value, "value must not be blank");
    }

    public static String notBlank(String value, String message) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(message);
        return value;
    }

    public static String notEmpty(String value) {
        return notEmpty(value, "value must not be empty");
    }

    public static String notEmpty(String value, String message) {
        if (value == null || value.isEmpty()) throw new IllegalArgumentException(message);
        return value;
    }

    public static String size(int min, int max, String value) {
        return size(min, max, value,
                "value length must be between " + min + " and " + max);
    }

    public static String size(int min, int max, String value, String message) {
        if (value == null || value.length() < min || value.length() > max)
            throw new IllegalArgumentException(message);
        return value;
    }

    public static <T extends Collection<?>> T size(int min, int max, T value) {
        return size(min, max, value,
                "collection size must be between " + min + " and " + max);
    }

    public static <T extends Collection<?>> T size(int min, int max, T value, String message) {
        if (value == null || value.size() < min || value.size() > max)
            throw new IllegalArgumentException(message);
        return value;
    }

    public static String length(int min, int max, String value) {
        return size(min, max, value,
                "value length must be between " + min + " and " + max);
    }

    public static String length(int min, int max, String value, String message) {
        return size(min, max, value, message);
    }

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$");

    public static String email(String value) {
        return email(value, "value must be a valid email address");
    }

    public static String email(String value, String message) {
        if (value == null || !EMAIL_PATTERN.matcher(value).matches())
            throw new IllegalArgumentException(message);
        return value;
    }

    public static String pattern(String regexp, String value) {
        return pattern(regexp, value, "value must match pattern: " + regexp);
    }

    public static String pattern(String regexp, String value, String message) {
        if (value == null || !value.matches(regexp))
            throw new IllegalArgumentException(message);
        return value;
    }

    private static final Pattern URL_PATTERN = Pattern.compile(
            "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$");

    public static String url(String value) {
        return url(value, "value must be a valid URL");
    }

    public static String url(String value, String message) {
        if (value == null || !URL_PATTERN.matcher(value).matches())
            throw new IllegalArgumentException(message);
        return value;
    }

    private static final Pattern ISBN_PATTERN = Pattern.compile(
            "^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$");

    public static String isbn(String value) {
        return isbn(value, "value must be a valid ISBN");
    }

    public static String isbn(String value, String message) {
        if (value == null || !ISBN_PATTERN.matcher(value).matches())
            throw new IllegalArgumentException(message);
        return value;
    }

    private static final Pattern EAN_PATTERN = Pattern.compile("^\\d{8}$|^\\d{13}$");

    public static String ean(String value) {
        return ean(value, "value must be a valid EAN barcode");
    }

    public static String ean(String value, String message) {
        if (value == null || !EAN_PATTERN.matcher(value).matches())
            throw new IllegalArgumentException(message);
        return value;
    }

    // Basic Luhn check for credit card
    public static String creditCard(String value) {
        return creditCard(value, "value must be a valid credit card number");
    }

    public static String creditCard(String value, String message) {
        if (value == null || !luhn(value.replaceAll("[\\s-]", "")))
            throw new IllegalArgumentException(message);
        return value;
    }

    private static boolean luhn(String number) {
        int sum = 0;
        boolean alternate = false;
        for (int i = number.length() - 1; i >= 0; i--) {
            if (!Character.isDigit(number.charAt(i))) return false;
            int n = number.charAt(i) - '0';
            if (alternate) { n *= 2; if (n > 9) n -= 9; }
            sum += n;
            alternate = !alternate;
        }
        return sum % 10 == 0;
    }

    // =========================================================================
    // Numeric — @Min @Max @Positive @PositiveOrZero @Negative @NegativeOrZero
    // int, long, double, float, short, byte + wrappers via autoboxing
    // =========================================================================

    // ── min ──────────────────────────────────────────────────────────────────

    public static int min(long min, int value) {
        return min(min, value, "value must be >= " + min);
    }
    public static int min(long min, int value, String message) {
        if (value < min) throw new IllegalArgumentException(message); return value;
    }

    public static long min(long min, long value) {
        return min(min, value, "value must be >= " + min);
    }
    public static long min(long min, long value, String message) {
        if (value < min) throw new IllegalArgumentException(message); return value;
    }

    public static double min(long min, double value) {
        return min(min, value, "value must be >= " + min);
    }
    public static double min(long min, double value, String message) {
        if (value < min) throw new IllegalArgumentException(message); return value;
    }

    public static float min(long min, float value) {
        return min(min, value, "value must be >= " + min);
    }
    public static float min(long min, float value, String message) {
        if (value < min) throw new IllegalArgumentException(message); return value;
    }

    public static short min(long min, short value) {
        return min(min, value, "value must be >= " + min);
    }
    public static short min(long min, short value, String message) {
        if (value < min) throw new IllegalArgumentException(message); return value;
    }

    public static byte min(long min, byte value) {
        return min(min, value, "value must be >= " + min);
    }
    public static byte min(long min, byte value, String message) {
        if (value < min) throw new IllegalArgumentException(message); return value;
    }

    public static BigDecimal min(long min, BigDecimal value) {
        return min(min, value, "value must be >= " + min);
    }
    public static BigDecimal min(long min, BigDecimal value, String message) {
        if (value == null || value.compareTo(BigDecimal.valueOf(min)) < 0)
            throw new IllegalArgumentException(message);
        return value;
    }

    // ── max ──────────────────────────────────────────────────────────────────

    public static int max(long max, int value) {
        return max(max, value, "value must be <= " + max);
    }
    public static int max(long max, int value, String message) {
        if (value > max) throw new IllegalArgumentException(message); return value;
    }

    public static long max(long max, long value) {
        return max(max, value, "value must be <= " + max);
    }
    public static long max(long max, long value, String message) {
        if (value > max) throw new IllegalArgumentException(message); return value;
    }

    public static double max(long max, double value) {
        return max(max, value, "value must be <= " + max);
    }
    public static double max(long max, double value, String message) {
        if (value > max) throw new IllegalArgumentException(message); return value;
    }

    public static float max(long max, float value) {
        return max(max, value, "value must be <= " + max);
    }
    public static float max(long max, float value, String message) {
        if (value > max) throw new IllegalArgumentException(message); return value;
    }

    public static short max(long max, short value) {
        return max(max, value, "value must be <= " + max);
    }
    public static short max(long max, short value, String message) {
        if (value > max) throw new IllegalArgumentException(message); return value;
    }

    public static byte max(long max, byte value) {
        return max(max, value, "value must be <= " + max);
    }
    public static byte max(long max, byte value, String message) {
        if (value > max) throw new IllegalArgumentException(message); return value;
    }

    public static BigDecimal max(long max, BigDecimal value) {
        return max(max, value, "value must be <= " + max);
    }
    public static BigDecimal max(long max, BigDecimal value, String message) {
        if (value == null || value.compareTo(BigDecimal.valueOf(max)) > 0)
            throw new IllegalArgumentException(message);
        return value;
    }

    // ── positive ──────────────────────────────────────────────────────────────

    public static int positive(int value) { return positive(value, "value must be positive"); }
    public static int positive(int value, String message) {
        if (value <= 0) throw new IllegalArgumentException(message); return value;
    }

    public static long positive(long value) { return positive(value, "value must be positive"); }
    public static long positive(long value, String message) {
        if (value <= 0) throw new IllegalArgumentException(message); return value;
    }

    public static double positive(double value) { return positive(value, "value must be positive"); }
    public static double positive(double value, String message) {
        if (value <= 0) throw new IllegalArgumentException(message); return value;
    }

    public static float positive(float value) { return positive(value, "value must be positive"); }
    public static float positive(float value, String message) {
        if (value <= 0) throw new IllegalArgumentException(message); return value;
    }

    public static BigDecimal positive(BigDecimal value) { return positive(value, "value must be positive"); }
    public static BigDecimal positive(BigDecimal value, String message) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException(message);
        return value;
    }

    // ── positiveOrZero ────────────────────────────────────────────────────────

    public static int positiveOrZero(int value) { return positiveOrZero(value, "value must be >= 0"); }
    public static int positiveOrZero(int value, String message) {
        if (value < 0) throw new IllegalArgumentException(message); return value;
    }

    public static long positiveOrZero(long value) { return positiveOrZero(value, "value must be >= 0"); }
    public static long positiveOrZero(long value, String message) {
        if (value < 0) throw new IllegalArgumentException(message); return value;
    }

    public static double positiveOrZero(double value) { return positiveOrZero(value, "value must be >= 0"); }
    public static double positiveOrZero(double value, String message) {
        if (value < 0) throw new IllegalArgumentException(message); return value;
    }

    public static float positiveOrZero(float value) { return positiveOrZero(value, "value must be >= 0"); }
    public static float positiveOrZero(float value, String message) {
        if (value < 0) throw new IllegalArgumentException(message); return value;
    }

    public static BigDecimal positiveOrZero(BigDecimal value) { return positiveOrZero(value, "value must be >= 0"); }
    public static BigDecimal positiveOrZero(BigDecimal value, String message) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException(message);
        return value;
    }

    // ── negative ──────────────────────────────────────────────────────────────

    public static int negative(int value) { return negative(value, "value must be negative"); }
    public static int negative(int value, String message) {
        if (value >= 0) throw new IllegalArgumentException(message); return value;
    }

    public static long negative(long value) { return negative(value, "value must be negative"); }
    public static long negative(long value, String message) {
        if (value >= 0) throw new IllegalArgumentException(message); return value;
    }

    public static double negative(double value) { return negative(value, "value must be negative"); }
    public static double negative(double value, String message) {
        if (value >= 0) throw new IllegalArgumentException(message); return value;
    }

    public static float negative(float value) { return negative(value, "value must be negative"); }
    public static float negative(float value, String message) {
        if (value >= 0) throw new IllegalArgumentException(message); return value;
    }

    public static BigDecimal negative(BigDecimal value) { return negative(value, "value must be negative"); }
    public static BigDecimal negative(BigDecimal value, String message) {
        if (value == null || value.compareTo(BigDecimal.ZERO) >= 0)
            throw new IllegalArgumentException(message);
        return value;
    }

    // ── negativeOrZero ────────────────────────────────────────────────────────

    public static int negativeOrZero(int value) { return negativeOrZero(value, "value must be <= 0"); }
    public static int negativeOrZero(int value, String message) {
        if (value > 0) throw new IllegalArgumentException(message); return value;
    }

    public static long negativeOrZero(long value) { return negativeOrZero(value, "value must be <= 0"); }
    public static long negativeOrZero(long value, String message) {
        if (value > 0) throw new IllegalArgumentException(message); return value;
    }

    public static double negativeOrZero(double value) { return negativeOrZero(value, "value must be <= 0"); }
    public static double negativeOrZero(double value, String message) {
        if (value > 0) throw new IllegalArgumentException(message); return value;
    }

    public static float negativeOrZero(float value) { return negativeOrZero(value, "value must be <= 0"); }
    public static float negativeOrZero(float value, String message) {
        if (value > 0) throw new IllegalArgumentException(message); return value;
    }

    public static BigDecimal negativeOrZero(BigDecimal value) { return negativeOrZero(value, "value must be <= 0"); }
    public static BigDecimal negativeOrZero(BigDecimal value, String message) {
        if (value == null || value.compareTo(BigDecimal.ZERO) > 0)
            throw new IllegalArgumentException(message);
        return value;
    }

    // =========================================================================
    // Decimal — @DecimalMin @DecimalMax @Digits
    // =========================================================================

    public static BigDecimal decimalMin(String min, BigDecimal value) {
        return decimalMin(min, value, "value must be >= " + min);
    }
    public static BigDecimal decimalMin(String min, BigDecimal value, String message) {
        if (value == null || value.compareTo(new BigDecimal(min)) < 0)
            throw new IllegalArgumentException(message);
        return value;
    }

    public static BigDecimal decimalMax(String max, BigDecimal value) {
        return decimalMax(max, value, "value must be <= " + max);
    }
    public static BigDecimal decimalMax(String max, BigDecimal value, String message) {
        if (value == null || value.compareTo(new BigDecimal(max)) > 0)
            throw new IllegalArgumentException(message);
        return value;
    }

    public static BigDecimal digits(int integer, int fraction, BigDecimal value) {
        return digits(integer, fraction, value,
                "value must have at most " + integer + " integer and " + fraction + " fraction digits");
    }
    public static BigDecimal digits(int integer, int fraction, BigDecimal value, String message) {
        if (value == null) throw new IllegalArgumentException(message);
        String plain = value.toPlainString().replace("-", "");
        String[] parts = plain.split("\\.");
        int intDigits = parts[0].equals("0") ? 0 : parts[0].length();
        int fracDigits = parts.length > 1 ? parts[1].length() : 0;
        if (intDigits > integer || fracDigits > fraction)
            throw new IllegalArgumentException(message);
        return value;
    }

    // =========================================================================
    // Temporal — @Past @PastOrPresent @Future @FutureOrPresent
    // Supports: LocalDate, LocalDateTime, ZonedDateTime, Instant, Date
    // =========================================================================

    public static LocalDate past(LocalDate value) {
        return past(value, "value must be in the past");
    }
    public static LocalDate past(LocalDate value, String message) {
        if (value == null || !value.isBefore(LocalDate.now()))
            throw new IllegalArgumentException(message);
        return value;
    }

    public static LocalDateTime past(LocalDateTime value) {
        return past(value, "value must be in the past");
    }
    public static LocalDateTime past(LocalDateTime value, String message) {
        if (value == null || !value.isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException(message);
        return value;
    }

    public static Instant past(Instant value) {
        return past(value, "value must be in the past");
    }
    public static Instant past(Instant value, String message) {
        if (value == null || !value.isBefore(Instant.now()))
            throw new IllegalArgumentException(message);
        return value;
    }

    public static LocalDate pastOrPresent(LocalDate value) {
        return pastOrPresent(value, "value must be in the past or present");
    }
    public static LocalDate pastOrPresent(LocalDate value, String message) {
        if (value == null || value.isAfter(LocalDate.now()))
            throw new IllegalArgumentException(message);
        return value;
    }

    public static LocalDateTime pastOrPresent(LocalDateTime value) {
        return pastOrPresent(value, "value must be in the past or present");
    }
    public static LocalDateTime pastOrPresent(LocalDateTime value, String message) {
        if (value == null || value.isAfter(LocalDateTime.now()))
            throw new IllegalArgumentException(message);
        return value;
    }

    public static Instant pastOrPresent(Instant value) {
        return pastOrPresent(value, "value must be in the past or present");
    }
    public static Instant pastOrPresent(Instant value, String message) {
        if (value == null || value.isAfter(Instant.now()))
            throw new IllegalArgumentException(message);
        return value;
    }

    public static LocalDate future(LocalDate value) {
        return future(value, "value must be in the future");
    }
    public static LocalDate future(LocalDate value, String message) {
        if (value == null || !value.isAfter(LocalDate.now()))
            throw new IllegalArgumentException(message);
        return value;
    }

    public static LocalDateTime future(LocalDateTime value) {
        return future(value, "value must be in the future");
    }
    public static LocalDateTime future(LocalDateTime value, String message) {
        if (value == null || !value.isAfter(LocalDateTime.now()))
            throw new IllegalArgumentException(message);
        return value;
    }

    public static Instant future(Instant value) {
        return future(value, "value must be in the future");
    }
    public static Instant future(Instant value, String message) {
        if (value == null || !value.isAfter(Instant.now()))
            throw new IllegalArgumentException(message);
        return value;
    }

    public static LocalDate futureOrPresent(LocalDate value) {
        return futureOrPresent(value, "value must be in the future or present");
    }
    public static LocalDate futureOrPresent(LocalDate value, String message) {
        if (value == null || value.isBefore(LocalDate.now()))
            throw new IllegalArgumentException(message);
        return value;
    }

    public static LocalDateTime futureOrPresent(LocalDateTime value) {
        return futureOrPresent(value, "value must be in the future or present");
    }
    public static LocalDateTime futureOrPresent(LocalDateTime value, String message) {
        if (value == null || value.isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException(message);
        return value;
    }

    public static Instant futureOrPresent(Instant value) {
        return futureOrPresent(value, "value must be in the future or present");
    }
    public static Instant futureOrPresent(Instant value, String message) {
        if (value == null || value.isBefore(Instant.now()))
            throw new IllegalArgumentException(message);
        return value;
    }

    // =========================================================================
    // Boolean — @AssertTrue @AssertFalse
    // =========================================================================

    public static boolean assertTrue(boolean value) {
        return assertTrue(value, "value must be true");
    }
    public static boolean assertTrue(boolean value, String message) {
        if (!value) throw new IllegalArgumentException(message);
        return true;
    }

    public static Boolean assertTrue(Boolean value) {
        return assertTrue(value, "value must be true");
    }
    public static Boolean assertTrue(Boolean value, String message) {
        if (value == null || !value) throw new IllegalArgumentException(message);
        return true;
    }

    public static boolean assertFalse(boolean value) {
        return assertFalse(value, "value must be false");
    }
    public static boolean assertFalse(boolean value, String message) {
        if (value) throw new IllegalArgumentException(message);
        return false;
    }

    public static Boolean assertFalse(Boolean value) {
        return assertFalse(value, "value must be false");
    }
    public static Boolean assertFalse(Boolean value, String message) {
        if (value == null || value) throw new IllegalArgumentException(message);
        return false;
    }

    // =========================================================================
    // Collection — @NotEmpty @Size
    // =========================================================================

    public static <T extends Collection<?>> T notEmpty(T value) {
        return notEmpty(value, "collection must not be empty");
    }
    public static <T extends Collection<?>> T notEmpty(T value, String message) {
        if (value == null || value.isEmpty()) throw new IllegalArgumentException(message);
        return value;
    }
}