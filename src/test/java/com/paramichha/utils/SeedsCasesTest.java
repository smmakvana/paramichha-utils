package com.paramichha.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.paramichha.utils.Cases.Returns;
import static com.paramichha.utils.Cases.ok;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Seeds cases. The frozen values matter: TestKit seeds generated tests from
 * {@code Seeds.stableHash(className)}, so a change here changes every generated test file.
 */
@DisplayName("Seeds cases")
class SeedsCasesTest {

    static Stream<Returns> cases() {
        return Stream.of(
                ok("frozen hash of a string", () -> Seeds.stableHash("abc"), -3351804022671199651L),
                ok("frozen hash of a class", () -> Seeds.stableHash(String.class), 7465538609921379402L),
                ok("frozen hash of two parts", () -> Seeds.stableHash("a", "bc"), 7096547112149444987L),
                ok("null string hashes to zero", () -> Seeds.stableHash((String) null), 0L));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("cases")
    @DisplayName("returns the expected value")
    void returnsExpected(Returns row) throws Throwable {
        assertThat(row.call().get()).as(row.name()).isEqualTo(row.expected());
    }

    @Test
    @DisplayName("the same input gives the same hash, a different input a different one")
    void sameInputSameHash() {
        assertThat(Seeds.stableHash("abc")).isEqualTo(Seeds.stableHash("abc"));
        assertThat(Seeds.stableHash("abc")).isNotEqualTo(Seeds.stableHash("abd"));
    }

    @Test
    @DisplayName("parts are separated, and a null part counts as an empty part")
    void partsAreSeparated() {
        assertThat(Seeds.stableHash("a", "bc")).isNotEqualTo(Seeds.stableHash("ab", "c"));
        assertThat(Seeds.stableHash("a", null, "b")).isEqualTo(Seeds.stableHash("a", "", "b"));
        assertThat(Seeds.stableHash("a", null, "b")).isNotEqualTo(Seeds.stableHash("a", "b"));
    }

    @Test
    @DisplayName("the same seed gives the same random sequence")
    void sameSeedSameRandom() {
        assertThat(Seeds.random(42).nextLong()).isEqualTo(Seeds.random(42).nextLong());
    }
}
