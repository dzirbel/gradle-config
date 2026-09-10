// Resolve the real publication instead of TestKit's separate injected plugin classloader. This keeps
// shared Kotlin API types identical in the settings plugin, project plugins, and configuration cache.
beforeSettings {
    val repository = providers.gradleProperty("gradleConfigRepository").get()
    val version = providers.gradleProperty("gradleConfigVersion").get()
    pluginManagement {
        repositories {
            maven { url = uri(repository) }
            gradlePluginPortal()
        }
        plugins {
            id("io.github.dzirbel.gradle-config") version version
        }
    }
}
