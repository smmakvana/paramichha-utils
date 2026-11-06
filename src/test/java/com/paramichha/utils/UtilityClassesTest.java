package com.paramichha.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

/** Every helper is a final class of static methods whose only constructor is private. */
class UtilityClassesTest {

    @ParameterizedTest
    @ValueSource(classes = {Boundary.class, DataValidation.class, Guard.class, JavaTypes.class,
            Seeds.class, Texts.class, Tokens.class})
    void cannotBeInstantiatedOrExtended(Class<?> type) throws Exception {
        assertThat(Modifier.isFinal(type.getModifiers())).isTrue();
        Constructor<?>[] constructors = type.getDeclaredConstructors();
        assertThat(constructors).hasSize(1);
        assertThat(Modifier.isPrivate(constructors[0].getModifiers())).isTrue();
        constructors[0].setAccessible(true);
        assertThat(constructors[0].newInstance()).isInstanceOf(type);
    }
}
