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

import com.google.common.truth.Correspondence;
import org.jspecify.annotations.NonNull;

/**
 * A factory of {@link Correspondence}s for constructing fluent assertions for collection elements.
 */
public final class Correspondences {

    /**
     * Prevents the utility class instantiation.
     */
    private Correspondences() {
    }

    /**
     * Obtains a {@link Correspondence} of an object to its type.
     *
     * <p>Elements of a collection can be matched to their class using this correspondence.
     *
     * <p>Example:
     * {@code
     * assertThat(objects)
     *     .comparingElementsUsing(type())
     *     .containsExactly(String.class, String.class);
     * }
     *
     * @param <T>
     *         type of the input object
     * @return correspondence by type
     */
    @SuppressWarnings("NullableProblems") // False positive.
    public static <T> Correspondence<T, @NonNull Class<?>> type() {
        return Correspondence.from(
                (o, cls) -> cls.isInstance(o), "is an instance of"
        );
    }
}
