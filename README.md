[![Ubuntu build][ubuntu-build-badge]][gh-actions]
[![codecov][codecov-badge]][codecov] &nbsp;
[![license][license-badge]][license]

# Spine Testlib

This module provides utilities for testing in Spine SDK subprojects. 
These utilities may also be handy for the users of the Spine SDK.

Spine Testlib relies on the following libraries:
  * [Google Protobuf](https://github.com/protocolbuffers/protobuf)
  * [Guava Testlib](https://github.com/google/guava/tree/master/guava-testlib)
  * [JUnit 5](https://junit.org/junit5/)
  * [Google Truth](https://github.com/google/truth) and its Java 8 and Protobuf extensions.

Dependencies on these libraries are exposed using the API scope to simplify dependency
management in user projects. Please see [build.gradle.kts] for details.

## Gradle dependency
To use Spine Testlib in your Gradle project:

```kotlin
dependencies {
    testImplementation("io.spine.tools:spine-testlib:${version}")
}
```

[codecov]: https://codecov.io/gh/SpineEventEngine/testlib
[codecov-badge]: https://codecov.io/gh/SpineEventEngine/testlib/branch/master/graph/badge.svg
[license-badge]: https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat
[license]: http://www.apache.org/licenses/LICENSE-2.0
[gh-actions]: https://github.com/SpineEventEngine/testlib/actions
[ubuntu-build-badge]: https://github.com/SpineEventEngine/testlib/actions/workflows/build-on-ubuntu.yml/badge.svg
