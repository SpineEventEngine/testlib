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

package io.spine.testing.given;

/**
 * Test environment for {@link io.spine.testing.TestsTest TestsTest}.
 */
public class AssertionsTestEnv {

    /** Prevents instantiation of this utility class. */
    private AssertionsTestEnv() {
    }

    public static class ClassWithPrivateCtor {
        @SuppressWarnings("RedundantNoArgConstructor") // We need this constructor for our tests.
        private ClassWithPrivateCtor() {}
    }

    public static class ClassWithPublicCtor {
        @SuppressWarnings("PublicConstructorInNonPublicClass") // It's the purpose of this
        // test class.
        public ClassWithPublicCtor() {}
    }

    public static class ClassThrowingExceptionInConstructor {
        private ClassThrowingExceptionInConstructor() {
            throw new AssertionError("This private constructor must not be called.");
        }
    }

    public static class ClassWithCtorWithArgs {
        @SuppressWarnings("unused")
        private final int id;
        private ClassWithCtorWithArgs(int id) { this.id = id;}
    }
}
