/*
 * Copyright 2024, TeamDev. All rights reserved.
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

import io.spine.internal.dependency.CheckerFramework
import io.spine.internal.dependency.Guava
import io.spine.internal.dependency.JUnit
import io.spine.internal.dependency.Kotest
import io.spine.internal.dependency.Protobuf
import io.spine.internal.dependency.Spine
import io.spine.internal.dependency.Truth
import io.spine.internal.gradle.checkstyle.CheckStyleConfig
import io.spine.internal.gradle.javadoc.JavadocConfig
import io.spine.internal.gradle.publish.IncrementGuard
import io.spine.internal.gradle.publish.PublishingRepos
import io.spine.internal.gradle.publish.spinePublishing
import io.spine.internal.gradle.report.license.LicenseReporter
import io.spine.internal.gradle.report.pom.PomGenerator
import io.spine.internal.gradle.standardToSpineSdk

plugins {
    `java-module`
    `kotlin-jvm-module`
    `compile-protobuf`
    idea
    jacoco
    `gradle-doctor`
    `project-report`
}
apply<IncrementGuard>()

apply(from = "$rootDir/version.gradle.kts")

group = "io.spine.tools"
version = rootProject.extra["versionToPublish"]!!

// Suppress `TooManyFunctions` for `TruthExtensions.kt` file.
detekt {
    baseline = file("detekt/detekt-baseline.xml")
}

repositories.standardToSpineSdk()

dependencies {
    compileOnly(CheckerFramework.annotations)

    /*
        Expose tools we use as transitive dependencies to simplify dependency
        management in projects that use Spine Testlib.
    */
    (Protobuf.libs
            + JUnit.api
            + Truth.libs
            + Guava.testLib
            + Kotest.assertions).forEach {
        api(it)
    }
    implementation(Spine.Logging.lib)

    @Suppress("DEPRECATION")
    run {
        val reason = "io.spine.testing.logging.LogTruth"
        implementation(io.spine.internal.dependency.Flogger.lib)?.because(reason)
        runtimeOnly(io.spine.internal.dependency.Flogger.Runtime.systemBackend)?.because(reason)
    }
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
