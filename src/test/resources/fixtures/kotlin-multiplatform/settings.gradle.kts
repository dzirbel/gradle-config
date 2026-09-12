plugins {
    // Dependency Analysis needs these plugins in the settings classloader.
    id("org.jetbrains.kotlin.multiplatform") version "2.4.10" apply false
    id("io.github.dzirbel.gradle-config")
}
rootProject.name = "kotlin-multiplatform-fixture"

// Explicitly align older stdlib requests from Kotlin compiler tools and KSP with this fixture's Kotlin version.
gradle.lifecycle.beforeProject {
    configurations.configureEach {
        resolutionStrategy.force("org.jetbrains.kotlin:kotlin-stdlib:2.4.10")
    }
}
