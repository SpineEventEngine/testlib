/*
 * Copyright 2025, TeamDev. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
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
package io.spine.testing.logging.mute

import com.google.common.collect.ImmutableSet
import com.google.errorprone.annotations.CanIgnoreReturnValue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.spine.logging.Logger
import io.spine.logging.LoggingFactory
import io.spine.logging.testing.ConsoleTap.install
import io.spine.logging.testing.tapConsole
import io.spine.testing.TestValues.randomString
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.nio.file.Path
import java.util.*
import java.util.function.Function
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExecutableInvoker
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.MediaType
import org.junit.jupiter.api.extension.TestInstances
import org.junit.jupiter.api.function.ThrowingConsumer
import org.junit.jupiter.api.parallel.ExecutionMode

@DisplayName("`MuteLogging` JUnit Extension should")
internal class MuteLoggingExtensionSpec {

    private lateinit var extension: MuteLoggingExtension

    @BeforeEach
    fun setUp() {
        extension = MuteLoggingExtension()
    }

    @Test
    fun `have public parameter-less constructor`() {
        val constructor = MuteLoggingExtension::class.java.getConstructor()
        val modifiers = constructor.modifiers
        Modifier.isPublic(modifiers) shouldBe true
    }

    @Test
    fun `print the standard output into std err stream if the test fails`() {
        extension.beforeEach(successfulContext())

        var errorMessage = ""
        val output = tapConsole {
            val stub = LoggingStub()
            errorMessage = stub.logError()

            extension.afterEach(failedContext())
        }

        output shouldContain errorMessage
    }

    @Test
    fun `mute Spine Logging API`() {
        val console = tapConsole {
            extension.beforeEach(successfulContext())

            val stub = LoggingStub()
            stub.logWarning()

            extension.afterEach(successfulContext())
        }

        console shouldBe ""
    }

    companion object {

        @BeforeAll
        @JvmStatic
        fun installConsoleTap() {
            install()
        }

        private fun successfulContext(): ExtensionContext {
            return StubContext(null)
        }

        private fun failedContext(): ExtensionContext {
            return StubContext(TestThrowable())
        }
    }
}

private class TestThrowable : Throwable() {
    companion object {
        @Suppress("unused")
        private const val serialVersionUID: Long = 2796411543401665435L
    }
}

/**
 * A stub class which performs logging operations using Spine Logging API.
 */
private class LoggingStub {

    @CanIgnoreReturnValue
    fun logWarning(): String {
        val warningMessage = "Warning: " + randomString()
        logger.atWarning().log { warningMessage }
        return warningMessage
    }

    @CanIgnoreReturnValue
    fun logError(): String {
        val errorMessage = "Error: " + randomString()
        logger.atError().log { errorMessage }
        return errorMessage
    }

    companion object {
        private val logger: Logger = LoggingFactory.forEnclosingClass()
    }
}

/**
 * Stub implementation of `ExtensionContext` which returns the given `Throwable`.
 */
private class StubContext(private val executionThrowable: Throwable?) : ExtensionContext {

    override fun getParent(): Optional<ExtensionContext> = Optional.empty()
    override fun getRoot(): ExtensionContext? = null
    override fun getUniqueId(): String? = null
    override fun getDisplayName(): String? = null
    override fun getTags(): Set<String> = ImmutableSet.of()
    override fun getElement(): Optional<AnnotatedElement> = Optional.empty()
    override fun getTestClass(): Optional<Class<*>> = Optional.empty()
    override fun getEnclosingTestClasses(): List<Class<*>?> = emptyList()
    override fun getTestInstanceLifecycle(): Optional<TestInstance.Lifecycle> = Optional.empty()
    override fun getTestInstance(): Optional<Any> = Optional.empty()
    override fun getTestInstances(): Optional<TestInstances> = Optional.empty()
    override fun getTestMethod(): Optional<Method> = Optional.empty()

    override fun getExecutionException(): Optional<Throwable> =
        Optional.ofNullable(executionThrowable)

    override fun getConfigurationParameter(key: String): Optional<String> = Optional.empty()

    override fun <T : Any> getConfigurationParameter(
        key: String,
        transformer: Function<String, T>
    ): Optional<T> = Optional.empty()

    override fun publishReportEntry(map: Map<String, String>) = Unit
    override fun publishFile(
        name: String?,
        mediaType: MediaType?,
        action: ThrowingConsumer<Path?>?
    ) = Unit

    override fun publishDirectory(
        name: String?,
        action: ThrowingConsumer<Path?>?
    ) = Unit

    override fun getStore(namespace: ExtensionContext.Namespace): ExtensionContext.Store? = null
    override fun getStore(
        scope: ExtensionContext.StoreScope?,
        namespace: ExtensionContext.Namespace?
    ): ExtensionContext.Store? = null

    override fun getExecutionMode(): ExecutionMode = ExecutionMode.SAME_THREAD
    override fun getExecutableInvoker(): ExecutableInvoker? = null
}
