pluginManagement { repositories { google(); gradlePluginPortal() } }
plugins {
    // Dependency Analysis needs these plugins in the settings classloader.
    id("com.android.library") version "9.4.0" apply false
    id("org.jetbrains.kotlin.android") version "2.4.10" apply false
    id("io.github.dzirbel.gradle-config")
}
rootProject.name = "kotlin-android-fixture"

// Explicitly align older stdlib requests from Kotlin compiler tools and KSP with this fixture's Kotlin version.
gradle.lifecycle.beforeProject {
    configurations.configureEach {
        resolutionStrategy.force("org.jetbrains.kotlin:kotlin-stdlib:2.4.10")
    }
}
