package io.github.dzirbel.gradleconfig

import org.gradle.api.provider.Property

/** Project conventions. Explicit Java toolchain configuration takes precedence. */
abstract class GradleConfigExtension {
    /**
     * JDK used for compilation, tests, and Java execution. Defaults to Java 25.
     * Each project has its own value; root overrides do not propagate to subprojects.
     * In the root project, also supplies the updateDaemonJvm default and the required daemon JVM version.
     * Explicit Java/Kotlin toolchains override compilation and execution only, not the daemon requirement.
     */
    abstract val javaLanguageVersion: Property<Int>
}
