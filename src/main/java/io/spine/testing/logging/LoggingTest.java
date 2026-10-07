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

package io.spine.testing.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Abstract base for tests of logging.
 */
public abstract class LoggingTest {

    /** The level to be used during the tests. */
    private final Level level;

    /** The interceptor of the logging operations. */
    private final Interceptor interceptor;

    /**
     * Creates a new test suite.
     *
     * @param loggingClass
     *         the class which performs the logging operations
     * @param level
     *         the level of logging in which we are interested in the tests
     */
    protected LoggingTest(Class<?> loggingClass, Level level) {
        this.level = checkNotNull(level);
        this.interceptor = new Interceptor(loggingClass, level);
    }

    /**
     * Obtains the instance of {@code AssertingHandler} of this test.
     *
     * @throws NullPointerException
     *          if the handler was not initialized or already removed
     * @see #interceptLogging()
     * @see #restoreLogging()
     */
    protected final LoggingAssertions assertLog() {
        return interceptor.assertLog();
    }

    /**
     * Obtains the class which logging operations are tested.
     */
    protected final Class<?> loggingClass() {
        return interceptor.loggingClass();
    }

    /**
     * Obtains the level of logging assigned for the tests.
     */
    protected final Level level() {
        return level;
    }

    /**
     * Obtains the level of the logging set for the logging class before the tests.
     */
    protected final Level previousLevel() {
        return interceptor.previousLevel();
    }

    /**
     * Redirects logging to a custom handler which would accumulate the log output.
     *
     * <p>The output can be later {@linkplain #assertLog() asserted}.
     *
     * <p>The logging will have the {@linkplain #level() level} assigned for the test.
     * The method also turns off {@linkplain Logger#setUseParentHandlers(boolean) parent
     * handlers}.
     *
     * @apiNote This method is not annotated {@code @BeforeEach} to allow derived test
     *         suites hook up the logging where appropriate to the test suite. In some cases
     *         the logger should be tuned <em>after</em> some of the operations performed in
     *         a test setup.
     * @see #restoreLogging()
     */
    protected final void interceptLogging() {
        interceptor.intercept();
    }

    /**
     * Removes the handler assigned in {@link #interceptLogging()} and restores the value
     * of the flag for using {@linkplain Logger#getUseParentHandlers() parent handlers}.
     *
     * <p>The {@linkplain #assertLog handler} is not available after this method is called until
     * it is created and added back by {@link #interceptLogging()}.
     */
    protected final void restoreLogging() {
        interceptor.release();
    }
}
