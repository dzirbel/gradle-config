plugins {
    id("io.github.dzirbel.gradle-config")
}

rootProject.name = "jvm-fixture"
include("default", "override", "explicit")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

