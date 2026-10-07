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

import io.spine.testing.given.SingletonTestEnv.EveryTimeNew;
import io.spine.testing.given.SingletonTestEnv.NoConstructor;
import io.spine.testing.given.SingletonTestEnv.PackagePrivateConstructor;
import io.spine.testing.given.SingletonTestEnv.ProtectedConstructor;
import io.spine.testing.given.SingletonTestEnv.SingletonClass;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("`SingletonTest` should")
class SingletonTestSpec {

    private SingletonTest<?> subject;

    /**
     * A test suite for correctly implemented singleton class.
     */
    private static SingletonTest<SingletonClass> positiveSuite() {
        return new SingletonTest<>(
                SingletonClass.class, SingletonClass::instance) {
        };
    }

    @Nested
    @DisplayName("check returning the same instance")
    class SameInstance {

        @Test
        @DisplayName("not throwing when the same")
        void correct() {
            subject = positiveSuite();

            assertPass(() -> subject.sameInstance());
        }

        @Test
        @DisplayName("throwing when not the same")
        void incorrect() {
            subject = new SingletonTest<>(EveryTimeNew.class, EveryTimeNew::instance) {
            };

            assertFails(() -> subject.sameInstance());
        }
    }

    @Nested
    @DisplayName("check preventing direct instantiation")
    class PreventingInstantiation {

        @Test
        @DisplayName("now throwing when the class has only private constructor(s)")
        void correct() {
            subject = positiveSuite();

            assertPass(() -> subject.ctorCheck());
        }

        @Test
        @DisplayName("throwing when no constructors are declared")
        void noConstructor() {
            subject = new SingletonTest<>(NoConstructor.class, NoConstructor::new) {
            };

            assertFails();
        }

        @Test
        @DisplayName("throwing when package-private constructor defined")
        void packagePrivateConstructor() {
            subject = new SingletonTest<>(
                    PackagePrivateConstructor.class, PackagePrivateConstructor::instance) {
            };

            assertFails();
        }

        @Test
        @DisplayName("throwing when protected constructor defined")
        void protectedConstructor() {
            subject = new SingletonTest<>(
                    ProtectedConstructor.class, ProtectedConstructor::instance) {
            };

            assertFails();
        }

        private void assertFails() {
            SingletonTestSpec.assertFails(() -> subject.ctorCheck());
        }
    }

    private static void assertPass(Executable executable) {
        assertDoesNotThrow(executable);
    }

    private static void assertFails(Executable executable) {
        assertThrows(AssertionError.class, executable);
    }
}
