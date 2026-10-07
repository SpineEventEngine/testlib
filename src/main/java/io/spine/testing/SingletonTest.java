/*
 * Copyright 2026 CodeMatters, Lda.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package io.spine.testing;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.testing.NullPointerTester.Visibility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.support.ModifierSupport;

import java.lang.reflect.Constructor;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.testing.NullPointerTester.Visibility.PUBLIC;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Abstract base for testing classes that implement a singleton pattern.
 *
 * @param <S> the type of the singleton
 */
public abstract class SingletonTest<S> extends ClassTest<S> {

    private final Supplier<S> accessor;

    /**
     * Creates new test suite.
     *
     * @param subject
     *          the class under the tests
     * @param minimalStaticMethodVisibility
     *          the minimal level of visibility of static methods for testing null parameters
     * @param accessor
     *          method reference to obtains the singleton
     */
    protected SingletonTest(Class<S> subject,
                            Visibility minimalStaticMethodVisibility,
                            Supplier<S> accessor) {
        super(subject, minimalStaticMethodVisibility);
        this.accessor = checkNotNull(accessor);
    }

    /**
     * Creates new test suite.
     *
     * @param subject
     *          the class under the tests
     * @param accessor
     *          method reference to obtain the singleton
     */
    protected SingletonTest(Class<S> subject, Supplier<S> accessor) {
        this(subject, PUBLIC, accessor);
    }

    @Test
    @DisplayName("return the same instance")
    void sameInstance() {
        assertSame(accessor.get(), accessor.get());
    }

    @Nested
    @DisplayName("prevent direct instantiation")
    class CheckConstructors {

        private final ImmutableList<Constructor<?>> constructors = ImmutableList.copyOf(
                subject().getDeclaredConstructors()
        );

        @Test
        @DisplayName("prohibiting non-private constructors")
        void prohibitNonPrivate() {
            var nonPrivateConstructors = constructors(ModifierSupport::isNotPrivate);

            assertThat(nonPrivateConstructors).isEmpty();
        }

        @Test
        @DisplayName("requiring at least one private constructor")
        void requirePrivate() {
            var privateConstructors = constructors(ModifierSupport::isPrivate);

            assertThat(privateConstructors).isNotEmpty();
        }

        private ImmutableList<Constructor<?>> constructors(Predicate<Constructor<?>> filter) {
            return constructors.stream()
                    .filter(filter)
                    .collect(toImmutableList());
        }
    }

    /**
     * Shortcut method to call method of {@link CheckConstructors} from a test suite which
     * tests this class.
     */
    @VisibleForTesting
    void ctorCheck() {
        var check = new CheckConstructors();
        check.prohibitNonPrivate();
        check.requirePrivate();
    }
}
