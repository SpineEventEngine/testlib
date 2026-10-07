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

import com.google.errorprone.annotations.CanIgnoreReturnValue;

import java.lang.reflect.Constructor;

/**
 * The abstract base for test object builders.
 *
 * @param <T>
 *         the result class
 */
public abstract class ReflectiveBuilder<T> {

    /** The class of the object we create. */
    private Class<T> resultClass;

    /** Constructor for use by subclasses. */
    protected ReflectiveBuilder() {
    }

    /**
     * Obtains constructor for the result object.
     */
    protected abstract Constructor<T> constructor();

    /**
     * Obtains the class of the object to build.
     */
    public Class<T> resultClass() {
        return this.resultClass;
    }

    /**
     * Sets the class of the object to build.
     */
    @CanIgnoreReturnValue
    protected ReflectiveBuilder<T> setResultClass(Class<T> resultClass) {
        this.resultClass = resultClass;
        return this;
    }

    /**
     * Creates the object being built.
     */
    public abstract T build();
}
