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

package io.spine.testing.logging.mute;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mutes all the logging for a certain test case or test suite.
 *
 * <p>Any kind of output into the standard output streams is blocked by this annotation.
 *
 * <p>If the test fails, the stack trace is printed into the standard error stream.
 *
 * <p>After the test completes, the standard output capabilities are restored.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(MuteLoggingExtension.class)
public @interface MuteLogging {

    /**
     * The reason this annotated test class or test method has logging muted.
     */
    String value() default "";
}
