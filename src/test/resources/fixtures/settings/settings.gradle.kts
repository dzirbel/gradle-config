plugins {
    id("io.github.dzirbel.gradle-config.settings") apply false
}

if (!providers.gradleProperty("relaxed").isPresent) {
    pluginManager.apply("io.github.dzirbel.gradle-config.settings")
}

rootProject.name = "settings-fixture"
