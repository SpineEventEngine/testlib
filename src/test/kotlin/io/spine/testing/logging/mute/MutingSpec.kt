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
import io.spine.logging.LoggingFactory
import io.spine.logging.testing.tapConsole
import io.spine.testing.TestValues
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("`withLoggingMutedIn` function should")
internal class MutingSpec {

    private val classes = listOf(TestValues::class, MutingSpec::class)
    private val loggers = classes.map { LoggingFactory.loggerFor(it) }

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
