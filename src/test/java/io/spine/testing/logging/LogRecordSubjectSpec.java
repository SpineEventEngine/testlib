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

import com.google.common.truth.ExpectFailure.SimpleSubjectBuilderCallback;
import com.google.common.truth.Subject;
import io.spine.testing.SubjectTest;
import io.spine.testing.TestValues;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import static com.google.common.truth.ExpectFailure.assertThat;
import static io.spine.testing.logging.LogRecordSubject.NO_LOG_RECORD;
import static io.spine.testing.logging.LogRecordSubject.records;

@DisplayName("`LogRecordSubject` should have")
class LogRecordSubjectSpec extends SubjectTest<LogRecordSubject, LogRecord> {

    private LogRecord record;
    private String msg;
    private Level level;
    private Throwable throwable;
    private Object[] parameters;

    @Override
    protected Subject.Factory<LogRecordSubject, LogRecord> subjectFactory() {
        return records();
    }

    @BeforeEach
    void createRecord() {
        msg = "Test log message" + TestValues.randomString();
        level = Level.FINE;
        record = new LogRecord(level, msg);
        throwable = new RuntimeException("Testing LogRecordSubject handling of Throwable");
        parameters = new Object[] { '0', "1", 2, 3L, 4.0f, 5.0d, true };
        record.setParameters(parameters);
        record.setThrown(throwable);
    }

    @Test
    @DisplayName("the check for no logged records")
    @SuppressWarnings("ResultOfMethodCallIgnored")  /* Intentionally. */
    void noRecords() {
        checkFails(whenTesting -> whenTesting.that(null).hasLevelThat());
        checkFails(whenTesting -> whenTesting.that(null).hasClassNameThat());
        checkFails(whenTesting -> whenTesting.that(null).hasMethodNameThat());
        checkFails(whenTesting -> whenTesting.that(null).hasMessageThat());
        checkFails(whenTesting -> whenTesting.that(null).hasParametersThat());
        checkFails(whenTesting -> whenTesting.that(null).hasThrowableThat());
        checkFails(whenTesting -> whenTesting.that(null).isDebug());
        checkFails(whenTesting -> whenTesting.that(null).isError());
    }

    private void
    checkFails(SimpleSubjectBuilderCallback<LogRecordSubject, LogRecord> assertionCallback) {
        var failure = expectFailure(assertionCallback);
        assertThat(failure)
                .factKeys()
                .contains(NO_LOG_RECORD);
    }

    @Test
    void hasMessageThat() {
        assertWithSubjectThat(record)
                .hasMessageThat()
                .isEqualTo(msg);

        var notExpected = TestValues.randomString();

        var failure = expectFailure(
                whenTesting -> whenTesting.that(record)
                                          .hasMessageThat()
                                          .isEqualTo(notExpected)
        );
        var assertFailure = assertThat(failure);
        assertFailure.factKeys()
                     .containsAnyOf(EXPECTED, BUT_WAS);
    }

    @Test
    void hasLevelThat() {
        assertWithSubjectThat(record)
                .hasLevelThat()
                .isEqualTo(level);

        expectSomeFailure(
                whenTesting -> whenTesting.that(record)
                                          .hasLevelThat()
                                          .isEqualTo(Level.OFF)
        );
    }

    @Test
    void isDebug() {
        assertWithSubjectThat(record)
                .isDebug();

        expectSomeFailure(whenTesting -> whenTesting.that(record)
                                                    .isError());
    }

    @Test
    void isError() {
        record.setLevel(Level.SEVERE);
        assertWithSubjectThat(record)
                .isError();

        expectSomeFailure(whenTesting -> whenTesting.that(record)
                                                    .isDebug());
    }

    @Test
    void hasParametersThat() {
        assertWithSubjectThat(record)
                .hasParametersThat()
                .asList()
                .containsExactlyElementsIn(parameters);

        expectSomeFailure(whenTesting -> whenTesting.that(record)
                                                    .hasParametersThat()
                                                    .isEmpty());
    }

    @Test
    void hasThrowableThat() {
        assertWithSubjectThat(record)
                .hasThrowableThat()
                .isInstanceOf(throwable.getClass());

        expectSomeFailure(whenTesting -> whenTesting.that(record)
                                                    .hasThrowableThat()
                                                    .isNull());
    }

    @Test
    void hasMethodThat() {
        var method = "hasMethodNameThat";
        record.setSourceMethodName(method);
        assertWithSubjectThat(record)
                .hasMethodNameThat()
                .isEqualTo(method);
        expectSomeFailure(whenTesting -> whenTesting.that(record)
                                                    .hasMethodNameThat()
                                                    .isEmpty());
    }

    @Test
    void hasClassThat() {
        var className = LogRecordSubjectSpec.class.getName();
        record.setSourceClassName(className);
        assertWithSubjectThat(record)
                .hasClassNameThat()
                .isEqualTo(className);
        expectSomeFailure(whenTesting -> whenTesting.that(record)
                                                    .hasClassNameThat()
                                                    .isEmpty());
    }
}
