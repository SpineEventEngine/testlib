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

package io.spine.testing;

import org.junit.jupiter.api.Tag;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks tests which are known to be slow and should not normally be run together with
 * the main test suite.
 *
 * <p>Slow tests typically are functional test, which may call network API, perform I/O operations,
 * spawn many threads and wait for execution, etc.
 *
 * <p>This annotation is an alias for {@code Tag("slow")}. Adding the {@code slow} tag on a test
 * case produces the same effect as adding this annotation.
 */
@Retention(RUNTIME)
@Target({TYPE, METHOD})
@Tag(SlowTest.TAG)
public @interface SlowTest {

    String TAG = "slow";
}
