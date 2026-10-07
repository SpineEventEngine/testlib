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

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.IOException;

/**
 * A JUnit {@link org.junit.jupiter.api.extension.Extension Extension} which mutes all the logs
 * for a test case.
 *
 * <p>Do not use this extension directly. Mark the target test method or class with
 * the {@link MuteLogging} annotation.
 *
 * @see MuteLogging
 */
public final class MuteLoggingExtension implements BeforeEachCallback, AfterEachCallback {

    private static final String ROOT = "";
    private final MutingLoggerTap loggerTap;
    /**
     * Creates new instance of the extension, redirecting to the stream which stores the output
     * into memory.
     */
    public MuteLoggingExtension() {
        this.loggerTap = new MutingLoggerTap(ROOT);
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        loggerTap.install();
    }

    @Override
    public void afterEach(ExtensionContext context) throws IOException {
        var exception = context.getExecutionException();
        if (exception.isPresent()) {
            loggerTap.flushToSystemErr();
        }
        loggerTap.remove();
    }
}
