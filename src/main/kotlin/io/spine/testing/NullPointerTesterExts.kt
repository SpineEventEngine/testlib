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

@file:JvmName("NullPointerTester")

package io.spine.testing

import com.google.common.testing.NullPointerTester

/**
 * Creates [NullPointerTester] and runs the [block] on it.
 */
public fun nullPointerTester(block: NullPointerTester.() -> NullPointerTester): NullPointerTester {
    val tester = NullPointerTester()
    tester.block()
    return tester
}

/**
 * Allows using generic parameter of the function instead of `MyType::class.java` as
 * the first parameter type.
 */
public inline fun <reified T : Any> NullPointerTester.setDefault(value: T): NullPointerTester =
    setDefault(T::class.java, value)


/**
 * Allows to use generic parameter of the function instead of `MyType::class.java` as the first
 * parameter type.
 */
public inline fun <reified T : Any> NullPointerTester.testAllPublicStaticMethods(): Unit =
    testAllPublicStaticMethods(T::class.java)

/**
 * Allows to use generic parameter of the function instead of `MyType::class.java` as the first
 * parameter type.
 */
public inline fun <reified T : Any> NullPointerTester.testAllPublicConstructors(): Unit =
    testAllPublicConstructors(T::class.java)
