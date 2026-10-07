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

import com.google.common.flogger.FluentLogger;
import com.google.common.truth.Subject;
import com.google.errorprone.annotations.InlineMe;
import org.jspecify.annotations.Nullable;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.common.truth.Truth.assert_;

/**
 * A set of static methods to begin a Truth assertion chain for logging types.
 */
public final class LogTruth {

    private static final String TRUTH_CALL_REPLACEMENT = "assert_().that(actual)";
    public static final String TRUTH_ASSERT_IMPORT = "com.google.common.truth.Truth.assert_";

    /** Prevents instantiation of this utility class. */
    private LogTruth() {
    }

    /**
     * Creates a subject for the passed logger.
     *
     * @deprecated Please use Spine Logging instead.
     */
    @SuppressWarnings("NonApiType")
    @Deprecated
    @InlineMe(replacement = TRUTH_CALL_REPLACEMENT, staticImports = TRUTH_ASSERT_IMPORT)
    public static Subject assertThat(@Nullable FluentLogger actual) {
        return assert_().that(actual);
    }

    /** Creates a subject for the passed record. */
    public static LogRecordSubject assertThat(@Nullable LogRecord record) {
        return assertAbout(LogRecordSubject.records()).that(record);
    }

    /** Creates a subject for the logging level. */
    public static Subject assertThat(@Nullable Level actual) {
        return assert_().that(actual);
    }

    /**
     * Creates a subject for the logging API.
     *
     * @deprecated Please use Spine Logging instead.
     */
    @SuppressWarnings("FloggerSplitLogStatement")
    /* See: https://github.com/SpineEventEngine/base/issues/612 */
    @Deprecated
    @InlineMe(replacement = TRUTH_CALL_REPLACEMENT, staticImports = TRUTH_ASSERT_IMPORT)
    public static Subject assertThat(FluentLogger.@Nullable Api actual) {
        return assert_().that(actual);
    }
}
