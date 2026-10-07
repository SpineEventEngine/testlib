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

buildscript {
    standardSpineSdkRepositories()
    doForceVersions(configurations)
    dependencies {
        classpath(io.spine.dependency.local.ToolBase.protobufSetupPlugins)
    }
}

plugins {
    id("module")
    id("com.google.protobuf")
    id("module-testing")
    `gradle-doctor`
    `project-report`
    `dokka-setup`
}
apply(plugin = "io.spine.descriptor-set-file")
apply(plugin = "io.spine.generated-sources")

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

configurations.all {
    resolutionStrategy {
        @Suppress("DEPRECATION")
        force(
            io.spine.dependency.lib.Flogger.lib,
            io.spine.dependency.lib.Flogger.Runtime.systemBackend,
        )
    }
}

spinePublishing {
    // We have to have a prefix for this library because it is going to be exposed
    // as API dependency from modules that are also, conventionally, called `testlib`.
    // Since Gradle attempts to resolve a dependency using Maven coordinates at
    // the build time, it will fail to resolve the `test-fixtures` capability if there are
    // two artifacts with the "same" coordinates, that is, `io.spine.tools:testlib:$version`.
    toolArtifactPrefix = "base-"
    destinations = with(PublishingRepos) {
        setOf(
            cloudArtifactRegistry,
            gitHub("testlib")
        )
    }
}

protobuf {
    protoc {
        artifact = Protobuf.compiler
    }
}

CheckStyleConfig.applyTo(project)
JavadocConfig.applyTo(project)
PomGenerator.applyTo(project)
LicenseReporter.generateReportIn(project)
LicenseReporter.mergeAllReports(project)
