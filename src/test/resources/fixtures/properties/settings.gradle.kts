plugins {
    id("io.github.dzirbel.gradle-config.settings")
}

rootProject.name = "properties-fixture"

buildCache {
    local {
        directory = file("build/local-cache")
    }
}

