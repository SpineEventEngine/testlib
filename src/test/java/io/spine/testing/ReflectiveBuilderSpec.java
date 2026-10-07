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

import com.google.protobuf.Any;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("`ReflectiveBuilder` should")
class ReflectiveBuilderSpec {

    @Test
    @DisplayName("have the result class")
    void resultClass() {
        var builder = new DummyBuilder().setResultClass(Any.class);
        assertEquals(Any.class, builder.resultClass());
    }

    @Test
    @DisplayName("obtain a constructor")
    void ctor() {
        assertNotNull(new DummyBuilder().constructor());
    }

    private static class DummyBuilder extends ReflectiveBuilder<Any> {

        @Override
        protected Constructor<Any> constructor() {
            Constructor<Any> ctor;
            try {
                ctor = Any.class.getDeclaredConstructor();
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(e);
            }
            return ctor;
        }

        @Override
        public Any build() {
            return Any.getDefaultInstance();
        }
    }
}
