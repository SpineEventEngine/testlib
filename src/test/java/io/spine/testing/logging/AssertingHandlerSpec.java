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

import io.spine.testing.TestValues;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("`AssertingHandler` should")
class AssertingHandlerSpec {

    private static final Logger logger = Logger.getLogger(AssertingHandlerSpec.class.getName());

    private AssertingHandler handler;

    @BeforeEach
    void setupHandler() {
        handler = new AssertingHandler();
        logger.setUseParentHandlers(false);
        logger.addHandler(handler);
        logger.setLevel(Level.INFO);
    }

    @AfterEach
    void clearHandler() {
        logger.removeHandler(handler);
    }

    @Test
    @DisplayName("assert no logs if nothing logged")
    void noLogs() {
        assertDoesNotThrow(handler::isEmpty);
    }

    @Test
    @DisplayName("throw `AssertionError` if there were logs")
    void throwIfLogged() {
        logger.info("Testing assertion");
        assertThrows(AssertionError.class, () -> handler.isEmpty());
    }

    @Test
    @DisplayName("obtain `StringSubject` for the first record")
    void textAssertion() {
        var msg = TestValues.randomString();
        logger.info(msg);
        handler.textOutput()
               .contains(msg);
    }
}
