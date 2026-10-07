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

import com.google.common.testing.NullPointerTester;
import com.google.common.testing.NullPointerTester.Visibility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.testing.NullPointerTester.Visibility.PUBLIC;
import static io.spine.testing.Assertions.assertTrue;

/**
 * Abstract base for test suites that test a class (e.g. static methods) rather than an object.
 *
 * @param <C>
 *         the class under the tests
 */
public abstract class ClassTest<C> {

    private final Class<C> subject;
    private final Visibility minimalStaticMethodVisibility;

    /**
     * Creates a new test suite for the passed class.
     *
     * @param subject
     *          the class to be tested
     * @param minimalStaticMethodVisibility
     *          the minimal level of visibility of static methods for testing
     *          null parameters
     * @see #configure(NullPointerTester)
     */
    protected ClassTest(Class<C> subject, Visibility minimalStaticMethodVisibility) {
        this.subject = checkNotNull(subject);
        this.minimalStaticMethodVisibility = checkNotNull(minimalStaticMethodVisibility);
    }

    /**
     * Creates a new test suite for the passed class.
     *
     * <p>This test suite will
     * {@link com.google.common.testing.NullPointerTester.Visibility#PUBLIC PUBLIC}
     * visibility of static methods for null-pointer testing.
     *
     * @param subject
     *          the class to be tested
     */
    protected ClassTest(Class<C> subject) {
        this(subject, PUBLIC);
    }

    /**
     * Obtains the class under tests.
     */
    protected final Class<C> subject() {
        return subject;
    }

    /**
     * Obtains the minimal level of visibility of static methods included into null-pointer
     * testing of parameters.
     *
     * @see #configure(NullPointerTester)
     */
    protected final Visibility minimalStaticMethodVisibility() {
        return minimalStaticMethodVisibility;
    }

    /**
     * Test handling null parameters of the static methods of the class.
     *
     * @see #configure(NullPointerTester)
     */
    @Test
    @DisplayName("not accept nulls in static methods if a parameter is non-Nullable")
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
        /* This test does assert via `NullPointerTester. */
    void nullCheckParamsOfStaticMethods() {
        var tester = new NullPointerTester();
        configure(tester);
        tester.testStaticMethods(subject(), minimalStaticMethodVisibility);
    }

    /**
     * A callback to configure a passed {@linkplain NullPointerTester}.
     *
     * <p>Does nothing. Override to specify default values in a derived test.
     */
    @SuppressWarnings("NoopMethodInAbstractClass") // We do not force overriding without a need.
    protected void configure(@SuppressWarnings("unused") NullPointerTester tester) {
        // Do nothing.
    }

    /**
     * Asserts that the class under tests has a {@code private} constructor
     * which accepts no parameters.
     */
    @SuppressWarnings("NewMethodNamingConvention")
    protected final void assertHasPrivateParameterlessCtor() {
        Assertions.assertHasPrivateParameterlessCtor(subject());
    }

    /**
     * Asserts that the class under tests is declared as {@code final}.
     */
    protected final void assertFinal() {
        assertTrue(Modifier.isFinal(subject().getModifiers()));
    }
}
