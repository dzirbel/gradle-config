# gradle-config

Opinionated Gradle settings plugin which enforces strict and bleeding-edge Gradle configuration options to the entire
project.

## Usage

Apply the settings plugin once in `settings.gradle.kts`:

```kotlin
plugins {
    id("io.github.dzirbel.gradle-config")
}
```

Requires Gradle 9.7.1+ and Java 21+.

## Features

### JVM Toolchain

JVM projects (those with the `java-base` plugin) are configured to use a Java 25 toolchain. Can be overridden by

```kotlin
gradleConfig {
    javaLanguageVersion = <...>
}
```

### Gradle Configuration options

The plugin directly sets some Gradle configuration options:

| Option                       | Type            | Value     |
|------------------------------|-----------------|-----------|
| `STABLE_CONFIGURATION_CACHE` | Feature preview | `enabled` |

Others are not set directly but their state is verified via a `checkGradleConfiguration` task, which is included in
`check`:

| Option                                                                              | Type                                  | Value  | On mismatch |
|-------------------------------------------------------------------------------------|---------------------------------------|--------|-------------|
| Configuration cache (`org.gradle.configuration-cache`)                              | Gradle property (`gradle.properties`) | `true` | Fail        |
| Build cache (`org.gradle.caching`)                                                  | Gradle property (`gradle.properties`) | `true` | Fail        |
| Parallel project execution (`org.gradle.parallel`)                                  | Gradle property (`gradle.properties`) | `true` | Fail        |
| Gradle warning mode (`org.gradle.warning.mode`)                                     | Gradle property (`gradle.properties`) | `fail` | Fail        |
| Kotlin DSL warnings-as-errors (`org.gradle.kotlin.dsl.allWarningsAsErrors`)         | Gradle property (`gradle.properties`) | `true` | Fail        |
| Configuration-cache problem policy (`org.gradle.configuration-cache.problems`)      | Gradle property (`gradle.properties`) | `fail` | Fail        |
| Isolated Projects (`org.gradle.isolated-projects`)                                  | Gradle property (`gradle.properties`) | `true` | Warn        |
| Parallel configuration-cache operations (`org.gradle.configuration-cache.parallel`) | Gradle property (`gradle.properties`) | `true` | Warn        |

## Development

This project is built and maintained with LLMs, but all user-facing documentation is handwritten. API surfaces and test
coverage and strictness are closely reviewed; implementation is typically less so.

- `./gradlew check` runs all tests and verification. Require JDKs 21 and 25.
- `./gradlew publishToMavenLocal` publishes the plugin to the local maven repository (`~/.m2/repository`) for use on the
  same machine
