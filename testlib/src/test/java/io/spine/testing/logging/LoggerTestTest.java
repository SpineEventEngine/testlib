/*
 * Copyright 2019, TeamDev. All rights reserved.
 *
 * Redistribution and use in source and/or binary forms, with or without
 * modification, must retain the above copyright notice and the following
 * disclaimer.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package io.spine.testing.logging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("`LoggerTest` should")
class LoggerTestTest {

    private LoggerTest test;
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
    @DisplayName("have no handler by default")
    void noHandler() {
        assertThrows(NullPointerException.class, test::handler);
    }

    @Test
    @DisplayName("assign handler")
    void assigningHandler() {
        test.addHandler();
        assertThat(test.handler())
                .isNotNull();
    }

    @Test
    @DisplayName("assign logging level")
    void assigningLevel() {
        assertThat(test.level())
                .isNotEqualTo(previousLevel);
        test.addHandler();
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

        test.addHandler();

        assertThat(jdkLogger().getLevel())
                .isEqualTo(newLevel);
    }

    @Test
    @DisplayName("clear handler")
    void clearingHandler() {
        test.removeHandler();
        assertThrows(NullPointerException.class, test::handler);
    }

    @Test
    @DisplayName("restore JDK logger level")
    void restoringLevel() {
        test.addHandler();
        test.removeHandler();
        assertThat(jdkLogger().getLevel())
                .isEqualTo(previousLevel);
    }

    private static class TestFixture extends LoggerTest {
        TestFixture(Class<?> loggingClass, Level level) {
            super(loggingClass, level);
        }
    }
}
