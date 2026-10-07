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
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class `'TempDir' should` {

    companion object {
        const val prefix = "TempDirTest"
        val tempDir: File = TempDir.withPrefix(prefix)
    }

    @Nested
    inner class `be created under the directory ` {

        @Test
        fun `from the 'System' property 'java-dot-io-dot-tmpdir'`() {
            assertThat(tempDir.toString())
                .contains(Testing.systemTempDir())
        }

        @Test
        fun `named after the package of 'TempDir' class`() {
            assertThat(tempDir.toString())
                .contains(TempDir::class.java.packageName)
        }
    }

    @Test
    fun `create an instance serving a test suite class`() {
        val thisClass = javaClass
        val tempDir = TempDir.forClass(thisClass)
        assertThat(tempDir.toString())
            .contains(thisClass.simpleName)
    }
}
