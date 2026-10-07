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

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Test environment for {@link io.spine.testing.SingletonTestTest}.
 */
public class SingletonTestEnv {

    private SingletonTestEnv() {
    }

    public static class SingletonClass {

        /** This field makes this class non-utility. */
        @SuppressWarnings({"FieldMayBeStatic", "unused"})
        private final boolean haveSomeState = true;

        private static final SingletonClass INSTANCE = new SingletonClass();

        /** Prevents direct instantiation. */
        private SingletonClass() {
        }

        public static SingletonClass instance() {
            return INSTANCE;
        }

        @SuppressWarnings("unused")
        public static void staticMethod(String param) {
            checkNotNull(param);
        }
    }

    public static class EveryTimeNew {

        /** This field makes this class non-utility. */
        @SuppressWarnings({"FieldMayBeStatic", "unused"})
        private final boolean haveState = true;

        private EveryTimeNew() {
        }

        public static EveryTimeNew instance() {
            return new EveryTimeNew();
        }
    }

    @SuppressWarnings("EmptyClass")
    public static class NoConstructor {
    }

    public static class PackagePrivateConstructor {

        private static final PackagePrivateConstructor INSTANCE = new PackagePrivateConstructor();

        /** This field makes this class non-utility. */
        @SuppressWarnings({"FieldCanBeLocal", "unused"})
        private final boolean state;

        PackagePrivateConstructor(boolean state) {
            this.state = state;
        }

        private PackagePrivateConstructor() {
            this(true);
        }

        public static PackagePrivateConstructor instance() {
            return INSTANCE;
        }
    }

    public static class ProtectedConstructor {

        private static final ProtectedConstructor INSTANCE = new ProtectedConstructor();

        /** This field makes this class non-utility. */
        @SuppressWarnings({"FieldCanBeLocal", "unused"})
        private final boolean state;

        protected ProtectedConstructor(boolean state) {
            this.state = state;
        }

        private ProtectedConstructor() {
            this(true);
        }

        public static ProtectedConstructor instance() {
            return INSTANCE;
        }
    }
}
