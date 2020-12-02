/*
 * Copyright 2020, TeamDev. All rights reserved.
 *
 * Redistribution and use in source and/or binary forms, with or without
 * modification, must retain the above copyright notice and the following
 * disclaimer.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
