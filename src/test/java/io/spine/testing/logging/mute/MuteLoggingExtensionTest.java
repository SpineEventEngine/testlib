/*
 * Copyright 2023, TeamDev. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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

package io.spine.testing.logging.mute;

import com.google.common.collect.ImmutableSet;
import com.google.common.flogger.FluentLogger;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.spine.testing.TestValues;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExecutableInvoker;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstances;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.reflect.Modifier.isPublic;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("UseOfSystemOutOrSystemErr") // Test std I/O overloading.
@DisplayName("`MuteLogging` JUnit Extension should")
class MuteLoggingExtensionTest extends SystemOutputTest {

    private MuteLoggingExtension extension;

    @BeforeEach
    void setUp() {
        out().reset();
        err().reset();
        extension = new MuteLoggingExtension();
    }

    @Test
    @DisplayName("have public parameter-less constructor")
    void ctor() throws NoSuchMethodException {
        var constructor = MuteLoggingExtension.class.getConstructor();
        var modifiers = constructor.getModifiers();
        assertTrue(isPublic(modifiers));
    }

    @Test
    @DisplayName("print the standard output into std err stream if the test fails")
    void printOutputOnException() throws IOException {
        extension.beforeEach(successfulContext());

        var stub = new LoggingStub();
        var errorMessage = stub.logError();

        extension.afterEach(failedContext());

        System.out.flush();
        System.err.flush();

        assertEquals(0, out().size());
        var actualErrorOutput = errorOutput();
        assertThat(actualErrorOutput).contains(errorMessage);
    }

    @Test
    @DisplayName("mute Spine Logging API")
    void muteSpineLogging() throws IOException {
        extension.beforeEach(successfulContext());

        var stub = new LoggingStub();
        stub.logWarning();

        extension.afterEach(successfulContext());

        assertEquals(0, out().size());
        assertEquals(0, err().size());
    }

    private static ExtensionContext successfulContext() {
        return new StubContext(null);
    }

    private static ExtensionContext failedContext() {
        return new StubContext(new TestThrowable());
    }

    private static class TestThrowable extends Throwable {
        private static final long serialVersionUID = 0L;
    }

    /**
     * Stub implementation of {@code ExtensionContext} which returns the passed {@code Throwable}.
     */
    @SuppressWarnings("ReturnOfNull")
    private static final class StubContext implements ExtensionContext {

        private final Throwable executionThrowable;

        private StubContext(@Nullable Throwable throwable) {
            this.executionThrowable = throwable;
        }

        @Override
        public Optional<ExtensionContext> getParent() {
            return Optional.empty();
        }

        @Override
        public ExtensionContext getRoot() {
            return null;
        }

        @Override
        public String getUniqueId() {
            return null;
        }

        @Override
        public String getDisplayName() {
            return null;
        }

        @Override
        public Set<String> getTags() {
            return ImmutableSet.of();
        }

        @Override
        public Optional<AnnotatedElement> getElement() {
            return Optional.empty();
        }

        @Override
        public Optional<Class<?>> getTestClass() {
            return Optional.empty();
        }

        @Override
        public Optional<TestInstance.Lifecycle> getTestInstanceLifecycle() {
            return Optional.empty();
        }

        @Override
        public Optional<Object> getTestInstance() {
            return Optional.empty();
        }

        @Override
        public Optional<TestInstances> getTestInstances() {
            return Optional.empty();
        }

        @Override
        public Optional<Method> getTestMethod() {
            return Optional.empty();
        }

        @Override
        public Optional<Throwable> getExecutionException() {
            return Optional.ofNullable(executionThrowable);
        }

        @Override
        public Optional<String> getConfigurationParameter(String key) {
            return Optional.empty();
        }

        @Override
        public <T> Optional<T> getConfigurationParameter(String key,
                                                         Function<String, T> transformer) {
            return Optional.empty();
        }

        @Override
        public void publishReportEntry(Map<String, String> map) {
        }

        @Override
        public Store getStore(Namespace namespace) {
            return null;
        }

        @Override
        public ExecutionMode getExecutionMode() {
            return ExecutionMode.SAME_THREAD;
        }

        @Override
        public ExecutableInvoker getExecutableInvoker() {
            return null;
        }
    }

    private static final class LoggingStub {

        private static final FluentLogger logger = FluentLogger.forEnclosingClass();

        @CanIgnoreReturnValue
        String logWarning() {
            var warningMessage = "Warning: " + TestValues.randomString();
            logger.atWarning().log(warningMessage);
            return warningMessage;
        }

        @CanIgnoreReturnValue
        String logError() {
            var errorMessage = "Error: " + TestValues.randomString();
            logger.atSevere().log(errorMessage);
            return errorMessage;
        }
    }
}
