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

import BuildSettings.javaVersion
import io.spine.dependency.build.CheckerFramework
import io.spine.dependency.build.ErrorProne
import io.spine.dependency.build.JSpecify
import io.spine.dependency.lib.Guava
import io.spine.dependency.lib.Jackson
import io.spine.dependency.lib.Protobuf
import io.spine.dependency.local.Logging
import io.spine.dependency.test.JUnit
import io.spine.dependency.test.Jacoco
import io.spine.gradle.checkstyle.CheckStyleConfig
import io.spine.gradle.github.pages.updateGitHubPages
import io.spine.gradle.javac.configureErrorProne
import io.spine.gradle.javac.configureJavac
import io.spine.gradle.javadoc.JavadocConfig
import io.spine.gradle.kotlin.applyJvmToolchain
import io.spine.gradle.kotlin.setFreeCompilerArgs
import io.spine.gradle.report.license.LicenseReporter

plugins {
    `java-library`
    id("net.ltgt.errorprone")
    kotlin("jvm")
    id("pmd-settings")
    id("org.jetbrains.kotlinx.kover")
    id("project-report")
    id("detekt-code-analysis")
    id("dokka-for-java")
    id("dokka-for-kotlin")
}

LicenseReporter.generateReportIn(project)
JavadocConfig.applyTo(project)
CheckStyleConfig.applyTo(project)

project.run {
    configureJava(javaVersion)
    configureKotlin(javaVersion)
    addDependencies()
    forceConfigurations()
    suppressMetadataValidationOfEnforcedPlatform()

    val generatedDir = "$projectDir/generated"
    setTaskDependencies(generatedDir)

    configureGitHubPages()
}

typealias Module = Project

fun Module.configureJava(javaVersion: JavaLanguageVersion) {
    java {
        toolchain.languageVersion.set(javaVersion)
    }

    tasks {
        withType<JavaCompile>().configureEach {
            configureJavac()
            configureErrorProne()
        }
    }
}

fun Module.configureKotlin(javaVersion: JavaLanguageVersion) {
    kotlin {
        applyJvmToolchain(javaVersion.asInt())
        explicitApi()
        compilerOptions {
            jvmTarget.set(BuildSettings.jvmTarget)
            setFreeCompilerArgs()
        }
    }

    kover {
        useJacoco(version = Jacoco.version)
        reports {
            total {
                xml {
                    onCheck = true
                }
            }
        }
    }
}

/**
 * These dependencies are applied to all subprojects and do not have to
 * be included explicitly.
 *
 * We expose production code dependencies as API because they are used
 * by the framework parts that depend on `base`.
 */
fun Module.addDependencies() = dependencies {
    errorprone(ErrorProne.core)
    api(JSpecify.annotations)
    Protobuf.libs.forEach { api(it) }
    api(Guava.lib)

    compileOnlyApi(CheckerFramework.annotations)
    ErrorProne.annotations.forEach {
        compileOnlyApi(it)
    }

    implementation(Logging.lib)
}

/**
 * Allows avoiding publishing errors when a dependency is enforced as a platform.
 *
 * The dependencies we already use as enforced platforms are multi-module toolkits
 *  [JUnit] or [Jackson].
 *
 * The error in Gradle looks like this:
 * ```
 * > Invalid publication 'mavenJava':
 *     - Variant 'runtimeElements' contains a dependency on enforced platform 'org.junit:junit-bom'
 *  In general publishing dependencies to enforced platforms is a mistake: enforced platforms
 *  shouldn't be used for published components because they behave like forced dependencies and
 *  leak to consumers. This can result in hard to diagnose dependency resolution errors.
 *  If you did this intentionally you can disable this check by adding 'enforced-platform' to
 *  the suppressed validations of the :generateMetadataFileForMavenJavaPublication task.
 *  For more on suppressing validations, please refer to
 *  https://docs.gradle.org/8.13/userguide/publishing_setup.html#sec:suppressing_validation_errors
 *  in the Gradle documentation.
 *  ```
 */
fun Module.suppressMetadataValidationOfEnforcedPlatform() {
    tasks.withType<GenerateModuleMetadata> {
        suppressedValidationErrors.add(JUnit.bom)
        suppressedValidationErrors.add(Jackson.bom)
    }
}

fun Module.forceConfigurations() {
    with(configurations) {
        forceVersions()
        excludeProtobufLite()
        all {
            resolutionStrategy {
                force(Logging.lib)
            }
        }
    }
}

fun Module.setTaskDependencies(generatedDir: String) {
    tasks {
        val cleanGenerated by registering(Delete::class) {
            delete(generatedDir)
        }
        clean.configure {
            dependsOn(cleanGenerated)
        }

        project.afterEvaluate {
            val publish = tasks.findByName("publish")
            publish?.dependsOn("${project.path}:updateGitHubPages")
        }
    }
    afterEvaluate {
        configureTaskDependencies()
    }
}

fun Module.configureGitHubPages() {
    val docletVersion = project.version.toString()
    updateGitHubPages(docletVersion) {
        allowInternalJavadoc.set(true)
        rootFolder.set(rootDir)
    }
}


