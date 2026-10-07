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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.logging.Level;

/**
 * Abstract base for logging tests that can setup and clean the logging fixture via
 * annotated methods.
 */
public abstract class SimpleLoggingTest extends LoggingTest {

    /**
     * Creates new test suite.
     *
     * @param loggingClass
     *         the class which performs the logging operations
     * @param level
     *         the level of logging we are interested in the tests
     */
    protected SimpleLoggingTest(Class<?> loggingClass, Level level) {
        super(loggingClass, level);
    }

    @BeforeEach
    void setupLogging() {
        interceptLogging();
    }

    @AfterEach
    void resetLogging() {
        restoreLogging();
    }
}
