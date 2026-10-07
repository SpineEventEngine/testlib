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

package io.spine.testing.logging;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.google.common.primitives.Bytes.asList;
import static com.google.common.truth.Truth.assertThat;
import static io.spine.testing.TestValues.random;
import static java.lang.Byte.MAX_VALUE;
import static java.lang.Byte.MIN_VALUE;

@DisplayName("`MemoizingStream` should")
class MemoizingStreamSpec {

    private static final byte[] EMPTY_BYTES = {};

    private MemoizingStream stream;

    @BeforeEach
    void createStream() {
        stream = new MemoizingStream();
    }

    @AfterEach
    void closeStream() throws IOException {
        stream.close();
    }

    @Nested
    @DisplayName("provide size of the stream")
    class Size {

        private int size;
        private byte[] input;

        @BeforeEach
        void generateInput() {
            size = random(1000);
            input = randomBytes(size);
        }

        @Test
        @DisplayName("equal to zero")
        void nothingWritten() {
            assertThat(stream.size())
                    .isEqualTo(0);
        }

        @Test
        @DisplayName("equal to size of written bytes")
        void inputWritten() throws IOException {
            stream.write(input);

            assertThat(stream.size())
                    .isEqualTo(size);
        }

        @Test
        @DisplayName("equal to zero after `reset()`")
        void clearOnReset() throws IOException {
            stream.write(input);
            stream.reset();

            assertThat(stream.size())
                    .isEqualTo(0);
        }
    }

    @Test
    @DisplayName("flush all the input")
    void flushEverything() throws IOException {
        var input = randomBytes(42);

        stream.write(input);

        checkMemoized(input);
    }

    @Test
    @DisplayName("not store flushed bytes")
    void clearAfterFlush() throws IOException {
        var input = randomBytes(12);

        stream.write(input);

        checkMemoized(input);
        checkMemoized(EMPTY_BYTES);
    }

    @Test
    @DisplayName("clear memoized bytes on demand")
    void clearOnDemand() throws IOException {
        var input = randomBytes(4);

        stream.write(input);
        stream.reset();

        checkMemoized(EMPTY_BYTES);
    }

    @Test
    @DisplayName("allow to clear memoized bytes any number of times")
    void clearAnyNumberOfTimes() throws IOException {
        var input = randomBytes(4);

        stream.write(input);

        checkMemoized(input);
        checkMemoized(EMPTY_BYTES);

        stream.reset();
        stream.reset();

        checkMemoized(EMPTY_BYTES);
    }

    @Test
    @DisplayName("store only lower bits if `int`")
    void negatives() throws IOException {
        stream.write(-1);
        stream.write(-42);
        stream.write(10);
        stream.write(0);
        stream.write(MIN_VALUE);

        checkMemoized(new byte[]{(byte)-1, (byte)-42, (byte) 10, (byte) 0, MIN_VALUE});
    }

    private void checkMemoized(byte[] expected) throws IOException {
        var outputCollector = new ByteArrayOutputStream();
        stream.flushTo(outputCollector);
        var actualBytes = outputCollector.toByteArray();

        assertThat(actualBytes)
                .asList()
                .containsExactlyElementsIn(asList(expected));
    }

    private static byte[] randomBytes(int count) {
        var result = new byte[count];
        for (var i = 0; i < count; i++) {
            @SuppressWarnings("NumericCastThatLosesPrecision") // OK because of the bounds.
            var randomByte = (byte) random(0, MAX_VALUE);
            result[i] = randomByte;
        }
        return result;
    }
}
