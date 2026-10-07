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

@file:JvmName("MoreAssertions")

package io.spine.testing

import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.spine.testing.Assertions.hasPrivateParameterlessCtor
import java.io.File
import java.nio.file.Path

/**
 * This file extends assertions provided in Java class [Assertions]
 * with those for Kotlin, while providing Java compatibility where it's possible.
 */
@Suppress("unused")
private const val ABOUT = ""

/**
 * Asserts that the given [file or directory][fileOrDir] exists.
 *
 * @param fileOrDir The file or directory to check.
 * @param message An optional error message. If not specified,
 *   the default message with the name of the file will be shown.
 */
@JvmOverloads
public fun assertExists(fileOrDir: File, message: String? = null) {
    FileExist(fileOrDir, message).check()
}

/**
 * Asserts that the given [file or directory][fileOrDir] exists.
 *
 * @param fileOrDir The file or directory to check.
 * @param message An optional error message.
 *   If not specified, the default message with the name of the file will be shown.
 */
@JvmOverloads
public fun assertExists(fileOrDir: Path, message: String? = null) {
    FileExist(fileOrDir, message).check()
}

/**
 * Asserts that the given [file or directory][fileOrDir] does not exist.
 *
 * @param fileOrDir The file or directory to check.
 * @param message An optional error message.
 *   If not specified, the default message with the name of the file will be shown.
 */
@JvmOverloads
public fun assertDoesNotExist(fileOrDir: File, message: String? = null) {
    FileExist(fileOrDir, message, not = true).check()
}

/**
 * Asserts that the given [file or directory][fileOrDir] does not exist.
 *
 * @param fileOrDir The file or directory to check.
 * @param message An optional error message.
 *   If not specified, the default message with the name of the file will be shown.
 */
@JvmOverloads
public fun assertDoesNotExist(fileOrDir: Path, message: String? = null) {
    FileExist(fileOrDir, message, not = true).check()
}

/**
 * Asserts file existence.
 *
 * @param fileOrDir An instance of [File] or [Path] to check.
 * @param message An optional error message.
 *   If not specified, the default message with the name of the file or directory will be shown.
 * @param not If `true` the object checks that the file does NOT exist.
 */
private class FileExist(
    private val fileOrDir: Any,
    private val message: String? = null,
    private val not: Boolean = false) {

    private fun message(): String = message ?: if (not) {
        "`$fileOrDir` should not exist, but it does."
    } else {
        "`$fileOrDir` expected to exist, but it does not."
    }

    private fun file(): File = when(fileOrDir) {
        is File ->  fileOrDir
        is Path -> fileOrDir.toFile()
        else -> error("$fileOrDir is neither `File` nor `Path`.")
    }

    fun check() {
        withClue(message()) {
            file().exists() shouldBe !not
        }
    }
}

/**
 * Tells if the class [C] has private constructor with no parameters.
 *
 * Usage:
 * ```
 * val hasCtor = hasPrivateParameterlessCtor<MyClass>()
 * ```
 */
public inline fun <reified C: Any> hasPrivateParameterlessCtor(): Boolean =
    hasPrivateParameterlessCtor(C::class.java)
