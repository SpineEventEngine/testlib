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

import com.google.protobuf.StringValue;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Utility factories for test values.
 */
public final class TestValues {

    /** Prevents instantiation of this utility class. */
    private TestValues() {
    }

    /**
     * Generates a string value based on {@linkplain java.util.UUID#randomUUID() generated UUID}.
     */
    public static String randomString() {
        return UUID.randomUUID()
                   .toString();
    }

    /**
     * Creates a random string based on {@linkplain java.util.UUID#randomUUID() generated UUID}
     * and the given prefix.
     */
    public static String randomString(String prefix) {
        checkNotNull(prefix);
        return prefix + randomString();
    }

    /**
     * Generates a {@code StringValue} with generated UUID.
     */
    public static StringValue newUuidValue() {
        var id = randomString();
        return StringValue.newBuilder()
                .setValue(id)
                .build();
    }

    /**
     * Generates a random integer in the range [0, max).
     */
    public static int random(int max) {
        return random(0, max);
    }

    /**
     * Generates a random integer in the range [min, max).
     */
    public static int random(int min, int max) {
        var randomNum = ThreadLocalRandom.current().nextInt(min, max);
        return randomNum;
    }

    /**
     * Generates a random long value in the range [min, max).
     */
    public static long longRandom(long min, long max) {
        var randomNum = ThreadLocalRandom.current().nextLong(min, max);
        return randomNum;
    }

    /**
     * Returns {@code null} always.
     *
     * <p>Use it when it is needed to pass {@code null} to a method in tests so that no
     * warnings suppression is needed.
     *
     * @apiNote The method doesn't take {@code Class<T>} for the sake of brevity.
     */
    @SuppressWarnings({
            "TypeParameterUnusedInFormals" /* See api note. */,
            "ConstantConditions", "ReturnOfNull" /* Returning of null is what we want here. */,
            "RedundantSuppression" /* To handle the IDEA issue with `ReturnOfNull` suppression. */
    })
    public static <T> T nullRef() {
        return null;
    }
}
