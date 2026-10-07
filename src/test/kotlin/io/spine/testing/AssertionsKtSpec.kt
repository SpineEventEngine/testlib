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

package io.spine.testing

import com.google.common.truth.Truth.assertThat
import java.io.File
import java.lang.AssertionError
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir

@DisplayName("`MoreAssertions` should")
internal class AssertionsKtSpec {

    companion object {

        lateinit var dir: File
        lateinit var file: File

        val missingFile = File("nowhere-near")

        @JvmStatic
        @BeforeAll
        fun createDirectory(@TempDir tempDir: File) {
            dir = tempDir
            file = dir.resolve("test.txt")
            file.writeText("Foo bar")
        }
    }

    @Nested
    inner class `assert existence of a 'File'` {

        @Nested
        inner class `not throwing when exists` {

            @Test
            fun file() = assertDoesNotThrow {
                assertExists(file)
            }

            @Test
            fun directory() = assertDoesNotThrow {
                assertExists(dir)
            }

            @Test
            fun `file as 'Path'`() = assertDoesNotThrow {
                assertExists(file.toPath())
            }

            @Test
            fun `directory as 'Path'`() = assertDoesNotThrow {
                assertExists(dir.toPath())
            }
        }

        @Nested
        inner class `throwing when does not exist with` {

            @Test
            fun `default message`() {
                val exception = assertThrows<AssertionError> {
                    assertExists(missingFile)
                }
                assertThat(exception).hasMessageThat().run {
                    contains(missingFile.name)
                    contains("expected to exist")
                    contains("but it does not")
                }
            }

            @Test
            fun `custom message`() {
                val exception = assertThrows<AssertionError> {
                    assertExists(missingFile, "Could not locate `$missingFile`.")
                }
                assertThat(exception).hasMessageThat().run {
                    contains(missingFile.name)
                    contains("Could not locate")
                    contains("`.")
                }
            }
        }
    }
}
