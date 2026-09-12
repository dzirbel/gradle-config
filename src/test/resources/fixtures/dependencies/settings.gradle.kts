plugins {
    id("io.github.dzirbel.gradle-config")
}

rootProject.name = "dependencies-fixture"
include(":child")

dependencyResolutionManagement {
    repositories {
        clear()
        maven { url = uri("repository") }
    }
}
