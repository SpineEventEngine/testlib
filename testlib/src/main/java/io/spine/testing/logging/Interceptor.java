/*
 * Copyright 2019, TeamDev. All rights reserved.
 *
 * Redistribution and use in source and/or binary forms, with or without
 * modification, must retain the above copyright notice and the following
 * disclaimer.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package io.spine.testing.logging;

import com.google.common.flogger.LoggerConfig;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.logging.Level;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Intercepts logging records of the associated class.
 */
public final class Interceptor {

    /** The class which performs the log operations. */
    private final Class<?> loggingClass;
    /** The helper to set logger configuration. */
    private final LoggerConfig config;
    /** The value of the property used by the logger before the test. */
    private boolean useParentHandler;
    /** The value the logger had before the tests. */
    private final Level previousLevel;
    /** The level to be used during the tests. */
    private final Level level;
    /** The handler which remembers log records and performs assertions. */
    private @Nullable AssertingHandler handler;

    /**
     * Creates a new instance for intercepting logging of the passed class at the specified
     * minimum level.
     */
    public Interceptor(Class<?> loggingClass, Level level) {
        this.loggingClass = checkNotNull(loggingClass);
        this.level = checkNotNull(level);
        this.config = LoggerConfig.getConfig(this.loggingClass);
        this.previousLevel = config.getLevel();
    }

    /**
     * Installs the handler for intercepting the records.
     *
     * <p>Current handlers are removed and remembered.
     * The logger will also not use parent handlers.
     *
     * @see #release()
     */
    public void intercept() {
        handler = new AssertingHandler();
        handler.setLevel(this.level);
        useParentHandler = config.getUseParentHandlers();
        config.setLevel(this.level);
        config.addHandler(handler);
        config.setUseParentHandlers(false);
    }

    /**
     * Returns the logger configuration to the previous state.
     */
    public void release() {
        if (handler == null) {
            return;
        }
        config.removeHandler(handler);
        config.setUseParentHandlers(useParentHandler);
        config.setLevel(previousLevel);
        handler = null;
    }

    /**
     * Obtains assertions for the accumulated log.
     *
     * @throws IllegalStateException
     *          if the interceptor is not yet {@linkplain #intercept() installed} or already
     *          {@linkplain #release() released}
     * @see #intercept()
     * @see #release()
     */
    public LoggingAssertions assertLog() {
        checkState(
                handler != null, "The handler is not available. Please call `intercept(Level)`."
        );
        return handler;
    }

    /**
     * Obtains the class which logging operations are tested.
     */
    public Class<?> loggingClass() {
        return loggingClass;
    }

    /**
     * Obtains the level of logging assigned for the tests.
     *
     * @throws IllegalStateException
     *          if the interceptor is not yet {@linkplain #intercept() installed} or already
     *          {@linkplain #release() released}
     */
    public Level level() {
        return checkNotNull(level);
    }

    /**
     * Obtains the level of the logging set for the logging class before the tests.
     */
    public Level previousLevel() {
        return previousLevel;
    }
}
