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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;

/**
 * Abstract base for tests that need to substitute {@link System#out} and {@link System#err}
 * for analyzing logging output.
 *
 * @deprecated Please use {@code tapConsole} from {@link io.spine.logging.testing} instead.
 */
@SuppressWarnings({
        "UseOfSystemOutOrSystemErr" /* Test std I/O substitution. */,
        "AbstractClassNeverImplemented" /* ... because of the deprecation. */
})
@Deprecated
public abstract class SystemOutputTest {

    private static final PrintStream originalOut = System.out;
    private static final PrintStream originalErr = System.err;
    private static final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private static final ByteArrayOutputStream err = new ByteArrayOutputStream();

    @BeforeAll
    static void substituteStreams() {
        System.setOut(newPrintStream(out));
        System.setErr(newPrintStream(err));
    }

    private static PrintStream newPrintStream(ByteArrayOutputStream stream) {
        return new PrintStream(stream);
    }

    private static Charset charset() {
        return Charset.defaultCharset();
    }

    @AfterAll
    static void restoreOriginalStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        out.reset();
        err.reset();
    }

    /**
     * Obtains the stream which accumulates system output.
     */
    protected static ByteArrayOutputStream out() {
        return out;
    }

    /**
     * Obtains the content of the system output accumulated so far.
     */
    protected static String output() {
        return toString(out);
    }

    /**
     * Obtains the stream which accumulates system error output.
     */
    protected static ByteArrayOutputStream err() {
        return err;
    }

    /**
     * Obtains the content of the system error output accumulated so far.
     */
    protected static String errorOutput() {
        return toString(err);
    }

    /**
     * Obtains the logging output accumulated so far.
     *
     * @apiNote By default Java Logging writes logging to {@code System.err}.
     *         This method is an alias to {@link #errorOutput()} so that the code of tests
     *         does not bring a confusion related to the "error" word in the context of logging.
     */
    protected static String loggingOutput() {
        return errorOutput();
    }

    private static String toString(ByteArrayOutputStream stream) {
        var result = stream.toString(charset());
        return result;
    }
}
