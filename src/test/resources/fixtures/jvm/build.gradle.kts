plugins {
    id("io.github.dzirbel.gradle-config")
}

check(!plugins.hasPlugin("java-base")) { "The conventions must not turn an aggregation project into a JVM project." }
