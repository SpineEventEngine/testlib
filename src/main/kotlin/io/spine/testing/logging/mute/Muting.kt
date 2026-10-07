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

@file:JvmName("Muting")

package io.spine.testing.logging.mute

import io.spine.logging.Level
import io.spine.logging.context.LogLevelMap
import io.spine.logging.context.ScopedLoggingContext

/**
 * Mutes logging for the loggers with the given names when executing the given [block].
 */
public fun withLoggingMutedIn(vararg loggerNames: String, block: () -> Unit) {
    withLoggingMutedIn(loggerNames.asList(), block)
}

/**
 * Mutes logging for the loggers with the given names when executing the given [block].
 */
public fun withLoggingMutedIn(loggerNames: Iterable<String>, block: () -> Unit) {
    val levels = loggerNames.associateWith { Level.OFF }
    val logLevelMap = LogLevelMap.create(levels)
    ScopedLoggingContext.getInstance()
        .newContext()
        .withLogLevelMap(logLevelMap)
        .call {
            block()
        }
}
