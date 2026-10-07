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

import com.google.common.truth.FailureMetadata;
import com.google.common.truth.ObjectArraySubject;
import com.google.common.truth.StringSubject;
import com.google.common.truth.Subject;
import com.google.common.truth.ThrowableSubject;
import org.jspecify.annotations.Nullable;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import static com.google.common.truth.Fact.simpleFact;

/**
 * Propositions for {@link LogRecord} subjects.
 */
public class LogRecordSubject extends Subject {

    static final String NO_LOG_RECORD = "no log record";

    private final @Nullable LogRecord actual;

    /** Obtains the factory for creating log record subjects for actual values. */
    static Subject.Factory<LogRecordSubject, LogRecord> records() {
        return LogRecordSubject::new;
    }

    private LogRecordSubject(FailureMetadata metadata, @Nullable LogRecord actual) {
        super(metadata, actual);
        this.actual = actual;
    }

    /** Returns a {@code StringSubject} to make assertions about the log record message. */
    public StringSubject hasMessageThat() {
        if (actual == null) {
            shouldExistButDoesNot();
            return ignoreCheck().that("");
        }
        var check = check("getMessage()");
        return check.that(actual.getMessage());
    }

    /** Obtains a subject for the logging level. */
    public Subject hasLevelThat() {
        if (actual == null) {
            shouldExistButDoesNot();
            return ignoreCheck().that((Object) null);
        }
        var check = check("getLevel()");
        var that = check.that(actual.getLevel());
        return that;
    }

    /** Asserts that the level of the record is {@code Level.FINE}. */
    public void isDebug() {
        hasLevelThat().isEqualTo(Level.FINE);
    }

    /** Asserts that the level of the record is {@code Level.SEVERE}. */
    public void isError() {
        hasLevelThat().isEqualTo(Level.SEVERE);
    }

    /** Obtains a subject for the logging event arguments. */
    public ObjectArraySubject hasParametersThat() {
        if (actual == null) {
            shouldExistButDoesNot();
            return ignoreCheck().that((Object[]) null);
        }
        var check = check("getParameters()");
        return check.that(actual.getParameters());
    }

    /** Obtains a subject for asserting {@code Throwable} associated with the log record. */
    public ThrowableSubject hasThrowableThat() {
        if (actual == null) {
            shouldExistButDoesNot();
            return ignoreCheck().that((Throwable) null);
        }
        var check = check("getThrown()");
        return check.that(actual.getThrown());
    }

    /** Obtains a subject for asserting the source method name associated with the log record. */
    public StringSubject hasMethodNameThat() {
        if (actual == null) {
            shouldExistButDoesNot();
            return ignoreCheck().that((String) null);
        }
        var check = check("getSourceMethodName()");
        return check.that(actual.getSourceMethodName());
    }

    /** Obtains a subject for asserting the source class name associated with the log record. */
    public StringSubject hasClassNameThat() {
        if (actual == null) {
            shouldExistButDoesNot();
            return ignoreCheck().that((String) null);
        }
        var check = check("getSourceClassName()");
        return check.that(actual.getSourceClassName());
    }

    private void shouldExistButDoesNot() {
        failWithoutActual(simpleFact(NO_LOG_RECORD));
    }
}
