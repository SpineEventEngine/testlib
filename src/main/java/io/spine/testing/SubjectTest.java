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

import com.google.common.truth.ExpectFailure.SimpleSubjectBuilderCallback;
import com.google.common.truth.Subject;

import static com.google.common.truth.ExpectFailure.expectFailureAbout;
import static com.google.common.truth.Truth.assertAbout;

/**
 * An abstract base for custom {@link Subject} test suites.
 *
 * @param <S>
 *         the type of the {@code Subject}
 * @param <T>
 *         the target type tested by the {@code Subject}
 */
public abstract class SubjectTest<S extends Subject, T> {

    protected static final String EXPECTED = "expected";
    protected static final String BUT_WAS = "but was";
    protected static final String EXPECTED_NOT_TO_BE = "expected not to be";
    protected static final String NULL = "null";

    protected abstract Subject.Factory<S, T> subjectFactory();

    /**
     * Creates a subject under the test with the passed actual value.
     */
    protected S assertWithSubjectThat(T actual) {
        return assertAbout(subjectFactory()).that(actual);
    }

    /**
     * Creates an {@code AssertionError} caused by the passed callback.
     *
     * <p>Example of usage:
     * <pre> {@code
     * AssertionError failure = expectFailure(whenTesting -> whenTesting.that(myType).hasProperty());
     * }</pre>
     */
    protected AssertionError
    expectFailure(SimpleSubjectBuilderCallback<S, T> assertionCallback) {
        return expectFailureAbout(subjectFactory(), assertionCallback);
    }

    /**
     * Expects that the passed callback causes {@code AssertionError} and ignores it.
     *
     * <p>This method is for test cases where the produced error is of no interest.
     * Example of usage:
     * <pre> {@code
     * expectSomeFailure(whenTesting -> whenTesting.that(myType).hasProperty());
     * }</pre>
     */
    @SuppressWarnings({"ThrowableNotThrown", "CheckReturnValue"}) // Ignore the AssertionError.
    protected void
    expectSomeFailure(SimpleSubjectBuilderCallback<S, T> assertionCallback) {
        expectFailureAbout(subjectFactory(), assertionCallback);
    }
}
