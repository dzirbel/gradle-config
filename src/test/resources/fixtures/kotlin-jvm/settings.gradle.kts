plugins {
    id("io.github.dzirbel.gradle-config")
}

rootProject.name = "kotlin-jvm-fixture"
include("default", "override", "java-explicit", "explicit", "target")
