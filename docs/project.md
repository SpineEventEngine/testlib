# Project: Spine Base TestLib

## Overview

Spine Base TestLib provides utilities for testing in the Spine SDK repositories.
SDK users may also find these utilities handy. It offers abstract base classes
for common test suites (`UtilityClassTest`, `ClassTest`, `SingletonTest`,
`SubjectTest`), test values and display names, Truth extensions and
correspondences, a base class for logging tests (`LoggingTest`) with assertions
for intercepted logging output (`LoggingAssertions`, `LogRecordSubject`), and
the `@MuteLogging` annotation, which applies a JUnit extension that blocks the
standard output streams for a test. It is published as
`io.spine.tools:base-testlib`.

## Architecture

Role in the org: a foundational **library**. `base-libraries`, `logging`,
`compiler`, `validation`, `core-jvm`, and most other SDK repositories depend on
it, typically from their tests. Among SDK repositories, it depends only on
`logging` at runtime; its build also uses the Protobuf setup plugins of `tool-base`.

- A single Gradle module with sources in Java and Kotlin under `io.spine.testing`,
  `io.spine.testing.logging`, and `io.spine.testing.logging.mute`. Its Protobuf
  types serve the module's own tests only.
- The module applies its own `module` convention
  (`buildSrc/src/main/kotlin/module.gradle.kts`) rather than the shared
  `jvm-module` one.
- Protobuf, JUnit Jupiter API, Truth, Guava TestLib, and Kotest assertions are
  exposed in the `api` scope, so a project that adds TestLib gets them too.
- The artifact carries the `base-` prefix: modules that consume it are also
  conventionally named `testlib`, and two artifacts with the same coordinates
  would clash when Gradle resolves the `test-fixtures` capability.
- `testlib` and `logging` depend on each other. `logging` builds against an
  already published `base-testlib`; so does this module, whose `module-testing`
  convention adds the previous release (`TestLib.lib`) to its own test classpath.
- The `LogTruth.assertThat()` overloads for Flogger types are deprecated in
  favour of Spine Logging and kept only for compatibility.

Read [`.agents/guidelines/jvm-project.md`](../.agents/guidelines/jvm-project.md) for the
build stack, coding style, tests, and versioning.
