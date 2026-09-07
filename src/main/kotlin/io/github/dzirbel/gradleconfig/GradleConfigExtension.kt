package io.github.dzirbel.gradleconfig

import org.gradle.api.provider.Property

/** Project conventions. Explicit Java toolchain configuration takes precedence. */
abstract class GradleConfigExtension {
    /** JDK used for compilation, tests, and Java execution. Defaults to Java 25. */
    abstract val javaLanguageVersion: Property<Int>
}
