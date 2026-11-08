package com.paramichha.utils;

import org.junit.jupiter.api.function.ThrowingSupplier;

/**
 * Behaviour cases written as data, so one parameterized test checks every row. A row is either a
 * call that must return a value, or a call that must throw a given exception with a given message.
 */
final class Cases {

    private Cases() {}

    /** A call that must return {@code expected}. */
    record Returns(String name, ThrowingSupplier<?> call, Object expected) {
        @Override
        public String toString() {
            return name;
        }
    }

    /** A call that must throw {@code type}; {@code message} is checked only when it is not null. */
    record Rejects(String name, ThrowingSupplier<?> call, Class<? extends Throwable> type, String message) {
        @Override
        public String toString() {
            return name;
        }
    }

    static Returns ok(String name, ThrowingSupplier<?> call, Object expected) {
        return new Returns(name, call, expected);
    }

    static Rejects no(String name, ThrowingSupplier<?> call, String message) {
        return new Rejects(name, call, IllegalArgumentException.class, message);
    }
}
