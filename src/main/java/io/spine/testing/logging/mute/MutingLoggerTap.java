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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import io.spine.testing.logging.MemoizingStream;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.logging.Handler;
import java.util.logging.Logger;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * Redirects the output of the associated JDK Logger to a memory stream.
 */
final class MutingLoggerTap {

    /** The loggerName of the associated logger. */
    private final String loggerName;

    // Previous state of the Logger that we amend.
    private boolean usedParentHandlers;
    private @Nullable MemoizingStream memoizingStream;
    private @Nullable Handler handler;
    private @Nullable ImmutableList<Handler> previousHandlers;

    /**
     * Creates the muting tap for the log with the passed name.
     *
     * @param loggerName
     *         the name of the logger given upon its {@linkplain Logger#getLogger(String) creation}
     */
    MutingLoggerTap(String loggerName) {
        this.loggerName = loggerName;
    }

    /**
     * Installs the tap on the logger.
     */
    synchronized void install() {
        memoizingStream = new MemoizingStream();
        createHandler();
        replaceHandlers();
    }

    /**
     * Creates a new handler copying configuration from the first handler of the logger found in
     * the nesting chain.
     *
     * @see #replaceHandlers()
     */
    private void createHandler() {
        var currentHandler = findHandler();
        var formatter = currentHandler.getFormatter();
        handler = new FlushingHandler(stream(), formatter);
        try {
            handler.setEncoding(currentHandler.getEncoding());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
        handler.setLevel(currentHandler.getLevel());
    }

    private Handler findHandler() {
        var logger = logger();
        while (logger.getHandlers().length == 0) {
            if (logger.getUseParentHandlers()) {
                logger = logger.getParent();
            }
        }
        return logger.getHandlers()[0];
    }

    /**
     * Replaces all handlers of the logger with the {@link #handler() tapping handler}.
     *
     * <p>The handlers and the flag for using {@linkplain Logger#getUseParentHandlers()
     * parent handlers} is remembered and then restored when the tap is
     * {@linkplain #remove() removed}.
     *
     * @see #createHandler()
     */
    private void replaceHandlers() {
        // Remember configuration of the logger.
        var logger = logger();
        usedParentHandlers = logger.getUseParentHandlers();
        previousHandlers = ImmutableList.copyOf(logger.getHandlers());
        for (var handler : previousHandlers) {
            logger.removeHandler(handler);
        }
        logger.addHandler(handler());
        logger.setUseParentHandlers(false);
    }

    /**
     * Restores the associated logger to the previous state, if the tap was installed.
     *
     * <p>Does nothing if the tap was not installed.
     */
    synchronized void remove() {
        if (handler == null) { // not installed.
            return;
        }
        var logger = logger();
        logger.removeHandler(handler());
        logger.setUseParentHandlers(usedParentHandlers);
        for (var previousHandler : previousHandlers()) {
            logger.addHandler(previousHandler);
        }
        stream().reset();

        memoizingStream = null;
        handler = null;
        previousHandlers = null;
    }

    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    synchronized void flushToSystemErr() throws IOException {
        flushTo(System.err);
    }

    @VisibleForTesting
    synchronized void flushTo(OutputStream outputStream) throws IOException {
        stream().flushTo(outputStream);
    }

    /**
     * Obtains the size of the associated stream, flushing it before querying the size.
     */
    @VisibleForTesting
    synchronized long streamSize() {
        var stream = stream();
        try {
            stream.flush();
        } catch (IOException e) {
            throw newIllegalStateException(e, "Error flushing `%s`.", stream);
        }
        return stream.size();
    }

    private Handler handler() {
        return requireNonNull(handler);
    }

    private MemoizingStream stream() {
        return requireNonNull(memoizingStream);
    }

    private ImmutableList<Handler> previousHandlers() {
        return requireNonNull(previousHandlers);
    }

    private Logger logger() {
        return Logger.getLogger(loggerName);
    }

    private static IllegalStateException
    newIllegalStateException(Throwable cause, String format, Object... args) {
        var errMsg = format(Locale.ROOT, format, args);
        throw new IllegalStateException(errMsg, cause);
    }
}
