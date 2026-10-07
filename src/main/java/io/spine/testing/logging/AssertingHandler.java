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

import com.google.common.truth.StringSubject;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static java.lang.System.lineSeparator;
import static java.util.Objects.requireNonNull;

/**
 * Test fixture for testing loggers based on JDK Logging.
 */
final class AssertingHandler extends Handler implements LoggingAssertions {

    private static final String FACT_NO_RECORDS = "There were no log records";

    private @Nullable List<LogRecord> logRecords = new ArrayList<>();

    @Override
    public void publish(LogRecord record) {
        if (isLoggable(record)) {
            logRecords().add(record);
        }
    }

    private List<LogRecord> logRecords() {
        return requireNonNull(logRecords, "The handler is already closed.");
    }

    @Override
    public StringSubject textOutput() {
        var logRecord = firstRecord();
        assertWithMessage(FACT_NO_RECORDS)
                .that(logRecord)
                .isNotNull();
        flush();
        var subject = assertThat(logRecordToString(logRecord));
        return subject;
    }

    @Override
    public LogRecordSubject record() {
        var logRecord = firstRecord();
        flush();
        var subject = LogTruth.assertThat(logRecord);
        return subject;
    }

    private LogRecord firstRecord() {
        assertThat(logRecords)
                .hasSize(1);
        return logRecords().get(0);
    }

    @Override
    public void isEmpty() {
        assertWithMessage("unexpected log recorded")
                .that(logRecords)
                .isEmpty();
    }

    private static String logRecordToString(LogRecord logRecord) {
        var sb = new StringBuilder();
        var message = new SimpleFormatter().formatMessage(logRecord);
        sb.append(logRecord.getLevel())
          .append(": ")
          .append(message)
          .append(lineSeparator());

        var thrown = logRecord.getThrown();
        if (thrown != null) {
            sb.append(thrown);
        }
        return sb.toString().trim();
    }

    @Override
    public void flush() {
        logRecords().clear();
    }

    @Override
    public void close() {
        logRecords = null;
    }
}
