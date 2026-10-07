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

import com.google.common.testing.NullPointerTester.Visibility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Abstract base for test suites testing utility classes.
 *
 * @param <C>
 *         the class under the tests
 */
public abstract class UtilityClassTest<C> extends ClassTest<C> {

    /**
     * Creates new test suite.
     *
     * @param subject
     *          the class under the tests
     * @param minimalStaticMethodVisibility
     *          the minimal level of visibility of static methods for testing null parameters
     */
    protected UtilityClassTest(Class<C> subject, Visibility minimalStaticMethodVisibility) {
        super(subject, minimalStaticMethodVisibility);
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
    protected UtilityClassTest(Class<C> subject) {
        super(subject);
    }

    @Test
    @DisplayName("have utility constructor")
    void hasUtilityConstructor() {
        assertHasPrivateParameterlessCtor();
    }

    @Test
    @DisplayName("be final")
    void checkFinal() {
        assertFinal();
    }
}
