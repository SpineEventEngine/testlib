/*
 * Copyright 2024, TeamDev. All rights reserved.
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

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.spine.logging.testing.ConsoleTap
import io.spine.logging.testing.tapConsole
import io.spine.testing.TestValues.randomString
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.charset.Charset
import java.util.logging.Logger
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("`MutingLoggerTap` should")
internal class MutingLoggerTapSpec {

    private val name: String = javaClass.name
    private val logger: Logger = Logger.getLogger(name)

    private lateinit var tap: MutingLoggerTap

    companion object {
        @BeforeAll
        @JvmStatic
        fun installTap() {
            ConsoleTap.install()
        }
    }

    @BeforeEach
    fun createTap() {
        tap = MutingLoggerTap(name)
    }

    @Nested internal inner class
    `when not installed, NOT intercept` {

        @Test
        fun `regular logging`() {
            val expected = "Test non interception."
            val output = tapConsole {
                logger.info(expected)
            }
            output shouldContain expected
        }

        @Test
        fun `error logging`() {
            val expectedError = "Testing error non interception."
            val output = tapConsole {
                logger.severe(expectedError)
            }
            output shouldContain expectedError
        }
    }

    @Nested internal inner class
    intercept {

        @BeforeEach
        fun install() = tap.install()

        @AfterEach
        fun remove() = tap.remove()

        @Test
        fun `regular logging`() {
            val msg = "Test interception."
            val output = tapConsole {
                logger.info(msg)
            }
            output shouldNotContain msg
        }

        @Test
        fun `error logging`() {
            val errorMessage = "Testing error interception."
            val output = tapConsole {
                logger.severe(errorMessage)
            }
            output shouldNotContain errorMessage
        }

        @Test
        fun `redirecting to 'MemoizingStream'`() {
            tap.streamSize() shouldBe 0
            val msg = randomString()

            logger.info(msg)

            (tap.streamSize() > 0) shouldBe true
        }

        @Nested internal inner class
        `flush to 'OutputStream'` {

            private lateinit var stream: ByteArrayOutputStream
            private lateinit var logMessage: String
            private lateinit var errorMessage: String

            @BeforeEach
            @Throws(IOException::class)
            fun flush() {
                stream = ByteArrayOutputStream()
                logMessage = "Testing log flushing. Random suffix: " + randomString()
                logger.info(logMessage)
                errorMessage = "Testing error flushing. Random suffix: " + randomString()
                logger.severe(errorMessage)
                tap.flushTo(stream)
            }

            @Test
            fun `accumulated output`() {
                val fo = flushedOutput()
                fo shouldContain logMessage
            }

            @Test
            fun `accumulated error output`() {
                val fo = flushedOutput()
                fo shouldContain errorMessage
            }

            private fun flushedOutput(): String = stream.toString(Charset.defaultCharset())
        }
    }
}
