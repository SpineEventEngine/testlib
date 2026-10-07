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
import com.google.common.truth.extensions.proto.ProtoTruth
import com.google.protobuf.ExtensionRegistry
import com.google.protobuf.Timestamp
import com.google.protobuf.TypeRegistry
import com.google.protobuf.timestamp
import java.util.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Truth extensions for Kotlin should provide")
internal class TruthExtensionsSpec {

    @Nested
    inner class `fun for 'StringSubject' allowing` {

        @Test
        fun `function block`() {
            assertThat("   foo  bar") {
                contains("foo")
                contains("bar")
            }
        }

        @Test
        fun `nested 'ignoringCase()' calls`() {
            assertThat("foo bar").ignoringCase() {
                contains("FOo")
                contains("BaR")
                doesNotContain("  ")
            }

            // And plain Truth syntax works too.
            assertThat("fiz baz").ignoringCase().contains("BaZ")
        }
    }

    @Test
    fun `fun for checking 'instanceOf'`() {
        assertThat(Timestamp.getDefaultInstance()) {
            isInstanceOf<Timestamp>()
        }
    }

    @Test
    fun `fun for 'IterableSubject'`() {
        assertThat(listOf(1, 2, 3)) {
            contains(1)
            contains(3)
            doesNotContain(4)
            containsAtLeastElementsIn(listOf(3, 2))
            containsAtLeastElementsIn(listOf(1, 3)).inOrder()
        }
    }

    @Test
    fun `fun for 'OptionalSubject'`() {
        assertThat(Optional.of("something")) {
            isPresent()
            hasValue("something")
        }
    }

    @Test
    fun `fun for 'ProtoSubject`() {
        val msg = prescription {
            prescribedOn = timestamp {
                seconds = System.currentTimeMillis() / 1000
            }
            prescribedDrug.add("Big bada boom")
        }

        val expected = prescription { prescribedDrug.add("Big bada boom") }

        // Standard syntax.
        ProtoTruth.assertThat(msg)
            .comparingExpectedFieldsOnly()
            .isEqualTo(expected)

        // Kotlin, having fun.
        assertThat(msg) {
            comparingExpectedFieldsOnly {
                isEqualTo(expected)
            }
        }

        // Given just to show nested call example for `unpackingAnyUsing`.
        assertThat(msg) {
            val typeRegistry = TypeRegistry.newBuilder().build()
            val extensionRegistry = ExtensionRegistry.newInstance()
            unpackingAnyUsing(typeRegistry, extensionRegistry) {
                comparingExpectedFieldsOnly {
                    isEqualTo(expected)
                }
            }
        }
    }
}
