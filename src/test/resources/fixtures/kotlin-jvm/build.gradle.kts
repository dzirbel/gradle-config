plugins {
    kotlin("jvm") version "2.4.10" apply false
}

check(!plugins.hasPlugin("java-base")) { "The conventions must not turn an aggregation project into a JVM project." }
