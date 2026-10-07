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

/**
 * A storage for the common JUnit 5 test
 * <a href="https://junit.org/junit5/docs/5.0.3/api/org/junit/jupiter/api/DisplayName.html">
 * display names</a>.
 *
 * <p>This class can be used to avoid string literal duplication when assigning {@code DisplayName}
 * to the common test cases.
 */
public final class DisplayNames {

    /**
     * A name for the test cases checking that a class has private parameterless (aka "utility")
     * constructor.
     */
    public static final String HAVE_PARAMETERLESS_CTOR = "have private parameterless constructor";

    /**
     * A name for the test cases checking that class methods do not accept {@code null} for their
     * non-{@linkplain org.jspecify.annotations.Nullable nullable} arguments.
     */
    public static final String NOT_ACCEPT_NULLS =
            "not accept nulls for non-Nullable method arguments";

    /**
     * Prevents instantiation of this class.
     */
    private DisplayNames() {
    }
}
