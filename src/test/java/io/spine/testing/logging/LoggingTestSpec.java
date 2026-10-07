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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.truth.Truth.assertThat;
import static io.spine.testing.Assertions.assertIllegalState;

@DisplayName("`LoggerTest` should")
class LoggingTestSpec {

    private LoggingTest test;
    private Level previousLevel;
    private Level newLevel;

    @BeforeEach
    void createFixture() {
        previousLevel = Level.SEVERE;
        newLevel = Level.FINE;
        jdkLogger().setLevel(previousLevel);
        test = new TestFixture(getClass(), newLevel);
    }

    private Logger jdkLogger() {
        return Logger.getLogger(getClass().getName());
    }

    @Test
    @DisplayName("assign logging class")
    void loggingClass() {
        assertThat(test.loggingClass())
                .isEqualTo(getClass());
        assertThat(test.level())
                .isEqualTo(newLevel);
    }

    @Test
    @DisplayName("do not intercept by default")
    void noHandler() {
        assertIllegalState(test::assertLog);
    }

    @Test
    @DisplayName("provide assertion API when installed")
    void assigningHandler() {
        test.interceptLogging();
        assertThat(test.assertLog())
                .isNotNull();
    }

    @Test
    @DisplayName("assign logging level")
    void assigningLevel() {
        assertThat(test.level())
                .isNotEqualTo(previousLevel);
        test.interceptLogging();
        assertThat(jdkLogger().getLevel())
                .isNotEqualTo(previousLevel);
    }

    @Test
    @DisplayName("remember previous level")
    void rememberingPreviousLevel() {
        assertThat(test.previousLevel())
                .isEqualTo(previousLevel);
    }

    @Test
    @DisplayName("set JDK logger new level when adding handler")
    void settingJdkLoggerLevel() {
        assertThat(jdkLogger().getLevel())
                .isEqualTo(previousLevel);

        test.interceptLogging();

        assertThat(jdkLogger().getLevel())
                .isEqualTo(newLevel);
    }

    @Test
    @DisplayName("do not provide assertions API after restored")
    void clearingHandler() {
        test.restoreLogging();
        assertIllegalState(test::assertLog);
    }

    @Test
    @DisplayName("restore JDK logger level")
    void restoringLevel() {
        test.interceptLogging();
        test.restoreLogging();
        assertThat(jdkLogger().getLevel())
                .isEqualTo(previousLevel);
    }

    private static class TestFixture extends LoggingTest {
        private TestFixture(Class<?> loggingClass, Level level) {
            super(loggingClass, level);
        }
    }
}
