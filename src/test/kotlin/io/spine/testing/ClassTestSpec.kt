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

import com.google.common.testing.NullPointerTester.Visibility
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("`ClassTest` should")
class ClassTestSpec {

    @Test
    fun `provide constructor with minimal static method visibility`() {
        val suite = object : ClassTest<StubTestSubject>(
            StubTestSubject::class.java,
            Visibility.PACKAGE
        ) {}

        suite.minimalStaticMethodVisibility() shouldBe Visibility.PACKAGE
    }

    @Test
    fun `assume 'PUBLIC' minimal visibility of static methods`() {
        val suite = object : ClassTest<StubTestSubject>(StubTestSubject::class.java) {}
        
        suite.minimalStaticMethodVisibility() shouldBe Visibility.PUBLIC
    }
}

class StubTestSubject
