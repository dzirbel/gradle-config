package io.github.dzirbel.gradleconfig

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.util.GradleVersion

class GradleConfigSettingsPlugin : Plugin<Settings> {
    override fun apply(settings: Settings) {
        requireSupportedGradle()
        settings.enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
    }
}

internal fun requireSupportedGradle() {
    if (GradleVersion.current() < GradleVersion.version("9.7.1")) {
        throw GradleException("gradle-config requires Gradle 9.7.1 or newer. Upgrade the consuming build's wrapper.")
    }
}
