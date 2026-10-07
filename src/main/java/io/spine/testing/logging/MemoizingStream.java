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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * An {@link OutputStream} which stores its input.
 */
public final class MemoizingStream extends OutputStream {

    private static final int ONE_MEBI_BYTE = 1024 * 1024;
    private final ByteArrayOutputStream memory;

    public MemoizingStream() {
        super();
        memory = new ByteArrayOutputStream(ONE_MEBI_BYTE);
    }

    @Override
    public void write(int b) {
        memory.write(b);
    }

    /**
     * Obtains the size of the memoized output in bytes.
     */
    public long size() {
        return memory.size();
    }

    /**
     * Clears the memoized output.
     */
    public void reset() {
        memory.reset();
    }

    /**
     * Copies the memoized input into the given stream and {@linkplain #reset() clears} memory.
     *
     * @param stream
     *         the target stream
     * @throws IOException
     *         if the target stream throws an {@link IOException} on a write operation
     */
    public synchronized void flushTo(OutputStream stream) throws IOException {
        var bytes = memory.toByteArray();
        stream.write(bytes);
        reset();
    }
}
