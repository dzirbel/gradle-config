plugins {
    id("org.jetbrains.kotlin.jvm") version "2.4.10" apply false
    id("io.github.dzirbel.gradle-config")
}

rootProject.name = "dependency-analysis-fixture"
include("leaf", "bridge", "transitive", "unused", "incorrect", "valid", "test-unused")
include("duplicate-leaf", "duplicates")
include("kotlin-transitive")

// Resolve the Kotlin compiler's older transitive stdlib request explicitly.
gradle.lifecycle.beforeProject {
    configurations.configureEach {
        resolutionStrategy.force("org.jetbrains.kotlin:kotlin-stdlib:2.4.10")
    }
}
