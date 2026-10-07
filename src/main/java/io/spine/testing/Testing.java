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
import io.spine.logging.Logger;
import io.spine.logging.LoggingFactory;
import kotlin.io.FilesKt;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.util.Locale;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.isDirectory;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Utilities for testing.
 */
public final class Testing {

    private static final Logger logger = LoggingFactory.forEnclosingClass();

    /** Prevent instantiation of this utility class. */
    private Testing() {
    }

    /**
     * Calls the given constructor to include it in the test coverage.
     *
     * <p>Some of the coding conventions may encourage throwing {@link AssertionError}
     * to prevent the instantiation of the target class, if it is designed as a utility class.
     * This method catches all the exceptions which may be thrown by the constructor.
     */
    @SuppressWarnings("OverlyBroadCatchBlock") // see Javadoc
    static void callConstructor(Constructor<?> constructor) {
        var accessible = constructor.canAccess(null);
        if (!accessible) {
            constructor.setAccessible(true);
        }
        try {
            constructor.newInstance();
        } catch (Exception ignored) {
            // Do nothing.
        } finally {
            if (!accessible) {
                constructor.setAccessible(false);
            }
        }
    }

    /**
     * Repeats the passed action the {@code count} number of times.
     */
    public static void repeat(int count, Runnable action) {
        checkNotNull(action);
        for (var i = 0; i < count; i++) {
             action.run();
        }
    }

    /**
     * Reports that a calling method should never be called by throwing {@link AssertionError}.
     *
     * @throws AssertionError always
     */
    public static void halt() throws AssertionError {
        fail("This method should never be called.");
    }

    private static IllegalStateException
    newIllegalStateException(Throwable cause, String format, Object... args) {
        var errMsg = format(Locale.ROOT, format, args);
        throw new IllegalStateException(errMsg, cause);
    }

    /**
     * Obtains the value of the {@code System} property for a temporary directory.
     */
    @SuppressWarnings("AccessOfSystemProperties")
    public static String systemTempDir() {
        return System.getProperty("java.io.tmpdir");
    }

    /**
     * Requests removal of the passed directory when the system shuts down.
     *
     * @implNote This method creates a new {@code Thread} for deleting the passed directory.
     *         That's why calling it should not be taken lightly. If your application creates
     *         several directories that need to be removed when JVM is terminated, consider
     *         gathering them under a common root passed to this method.
     * @see Runtime#addShutdownHook(Thread)
     */
    static void deleteRecursivelyOnShutdownHook(Path directory) {
        checkNotNull(directory);
        var runtime = Runtime.getRuntime();
        runtime.addShutdownHook(new Thread(() -> deleteRecursively(directory)));
    }

    /**
     * Ensures that the specified directory exists, creating it, if it was not done
     * prior to this call.
     *
     * <p>If the given path exists, but refers to a file, the method
     * throws {@link IllegalStateException}.
     *
     * @return the passed instance
     * @throws IllegalStateException
     *          if the passed path represents existing file, instead of a directory
     */
    @CanIgnoreReturnValue
    static Path ensureDirectory(Path directory) {
        if (!exists(directory)) {
            try {
                createDirectories(directory);
            } catch (IOException e) {
                throw newIllegalStateException(e, "Unable to create `%s`.", directory);
            }
        } else {
            checkState(
                    isDirectory(directory),
                    "The path `%s` exists, but it is not a directory.", directory
            );
        }
        return directory;
    }

    /**
     * Deletes the passed directory.
     *
     * <p>If the operation fails, the method returns {@code false}. In such a case,
     * the content of the directory may be partially deleted.
     *
     * @param directory
     *          the directory to delete
     * @return {@code true} if the directory was successfully deleted, {@code false} otherwise
     */
    @CanIgnoreReturnValue
    private static boolean deleteRecursively(Path directory) {
        var success = FilesKt.deleteRecursively(directory.toFile());
        if (!success) {
            logger.atWarning()
                  .log(() -> format("Unable to delete the directory `%s`.", directory));
        }
        return success;
    }
}
