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
import io.spine.logging.LoggingFactory
import io.spine.logging.testing.ConsoleTap
import io.spine.logging.testing.tapConsole
import io.spine.testing.TestValues
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("`withLoggingMutedIn` function should")
internal class MutingSpec {

    private val classes = listOf(TestValues::class, MutingSpec::class)
    private val loggers = classes.map { LoggingFactory.loggerFor(it) }

    companion object {
        @BeforeAll
        @JvmStatic
        fun installTap() {
            ConsoleTap.install()
        }
    }

    @Test
    fun `mute logging for all loggers with the given name`() {
        var consoleOutput: String
        val loggerNames = classes.map { it.qualifiedName!! }

        // Check that loggers do produce console output when not muted.
        val visibleMessage = "This should be visible."
        consoleOutput = tapConsole {
            loggers.forEach {
                it.atError().log { visibleMessage }
            }
        }
        consoleOutput shouldContain visibleMessage
        consoleOutput.occurrencesOf(visibleMessage) shouldBe loggers.size

        // Check that the console does not have logging output when muted.
        withLoggingMutedIn(loggerNames) {
            val logMessage = "Should not be visible"
             consoleOutput = tapConsole {
                loggers.forEach {
                    it.atError().log { logMessage }
                }
            }

            consoleOutput shouldNotContain logMessage
        }
    }
}

private fun String.occurrencesOf(substring: String) = split(substring).size - 1
