# Project: Spine Base TestLib

## Overview

Spine Base TestLib provides utilities for testing in the Spine SDK repositories,
which SDK users may also find handy. It offers abstract base classes for common
test suites (`UtilityClassTest`, `ClassTest`, `SingletonTest`, `SubjectTest`),
test values and display names, Truth extensions and correspondences, assertions
for logging (`LoggingTest`, `LogRecordSubject`), and the `@MuteLogging` JUnit
extension, which blocks the standard output streams for a test. It is published
as `io.spine.tools:base-testlib`.

## Architecture

Role in the org: a foundational **library**. `base-libraries`, `logging`,
`compiler`, `validation`, `core-jvm`, and most other SDK repositories depend on
it, typically from their tests. At runtime it depends on `logging` only; its
build also uses the protobuf setup plugins of `tool-base`.

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
- `testlib` and `logging` depend on each other. Each builds against the other's
  previously published version, so the last `base-testlib` release appears on
  this module's own test classpath through `Logging.testLib`.
- The Flogger-based `LogTruth` API is deprecated in favour of Spine Logging and
  kept only for compatibility.

Read [`.agents/guidelines/jvm-project.md`](../.agents/guidelines/jvm-project.md) for the
build stack, coding style, tests, and versioning.
