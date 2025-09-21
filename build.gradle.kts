/*
 * Copyright 2025, TeamDev. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
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

import io.spine.dependency.build.CheckerFramework
import io.spine.dependency.lib.Guava
import io.spine.dependency.lib.Protobuf
import io.spine.dependency.local.Logging
import io.spine.dependency.test.JUnit
import io.spine.dependency.test.Kotest
import io.spine.dependency.test.Truth
import io.spine.gradle.checkstyle.CheckStyleConfig
import io.spine.gradle.javadoc.JavadocConfig
import io.spine.gradle.publish.IncrementGuard
import io.spine.gradle.publish.PublishingRepos
import io.spine.gradle.publish.spinePublishing
import io.spine.gradle.repo.standardToSpineSdk
import io.spine.gradle.report.license.LicenseReporter
import io.spine.gradle.report.pom.PomGenerator

plugins {
    id("module")
    `compile-protobuf`
    id("module-testing")
    `gradle-doctor`
    `project-report`
}
apply<IncrementGuard>()

apply(from = "$rootDir/version.gradle.kts")

group = "io.spine.tools"
version = rootProject.extra["versionToPublish"]!!

// Suppress `TooManyFunctions` for the `TruthExtensions.kt` file.
detekt {
    baseline = file("detekt/detekt-baseline.xml")
}

repositories.standardToSpineSdk()

dependencies {
    compileOnly(CheckerFramework.annotations)

    implementation(platform(JUnit.bom))

    /*
        Expose tools we use as transitive dependencies to simplify dependency
        management in projects that use Spine TestLib.
    */
    (Protobuf.libs
            + JUnit.Jupiter.api
            + Truth.libs
            + Guava.testLib
            + Kotest.assertions)
        .forEach {
            api(it)
        }
    implementation(Logging.lib)

    @Suppress("DEPRECATION")
    run {
        val reason = "io.spine.testing.logging.LogTruth"
        implementation(io.spine.dependency.lib.Flogger.lib)?.because(reason)
        runtimeOnly(io.spine.dependency.lib.Flogger.Runtime.systemBackend)?.because(reason)
    }

    testImplementation(JUnit.Jupiter.engine)
    testImplementation(Logging.testLib)
    testImplementation(Logging.stdContext)?.because(
        "We need logging context support in logging tests."
    )
}

spinePublishing {
    destinations = with(PublishingRepos) {
        setOf(
            cloudArtifactRegistry,
            gitHub("testlib")
        )
    }
    dokkaJar {
        java = true
    }
}

CheckStyleConfig.applyTo(project)
JavadocConfig.applyTo(project)
PomGenerator.applyTo(project)
LicenseReporter.generateReportIn(project)
LicenseReporter.mergeAllReports(project)
