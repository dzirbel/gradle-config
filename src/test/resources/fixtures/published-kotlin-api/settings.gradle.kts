pluginManagement {
    repositories {
        maven { url = uri("../../repository") }
        gradlePluginPortal()
    }
}

plugins {
    id("io.github.dzirbel.gradle-config") version providers.gradleProperty("gradleConfigVersion").get()
}

rootProject.name = "published-kotlin-api-fixture"
include("kotlin")
