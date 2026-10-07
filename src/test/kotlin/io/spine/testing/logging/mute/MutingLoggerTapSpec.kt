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
