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

import com.google.common.truth.StringSubject;

/**
 * Interface for asserting intercepted logging output.
 */
public interface LoggingAssertions {

    /**
     * Asserts that there were no log messages.
     */
    void isEmpty();

    /**
     * Obtains the subject for asserting text output of the first log record.
     */
    StringSubject textOutput();

    /**
     * Obtains the subject for the only log record placed to the log.
     *
     * @throws AssertionError if the were no records or more than one log record
     */
    LogRecordSubject record();
}
