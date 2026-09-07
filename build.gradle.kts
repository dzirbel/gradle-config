plugins {
    `kotlin-dsl`
    `maven-publish`
}

group = "io.github.dzirbel"
version = "0.1.0-SNAPSHOT"

kotlin {
    jvmToolchain(21)
    compilerOptions.allWarningsAsErrors = true
}

dependencies {
    testImplementation(kotlin("test"))
}

gradlePlugin {
    plugins {
        register("gradleConfig") {
            id = "io.github.dzirbel.gradle-config"
            implementationClass = "io.github.dzirbel.gradleconfig.GradleConfigPlugin"
            displayName = "Shared Gradle project configuration"
            description = "JVM toolchains, Gradle script formatting, and recommended Gradle properties."
        }
        register("gradleConfigSettings") {
            id = "io.github.dzirbel.gradle-config.settings"
            implementationClass = "io.github.dzirbel.gradleconfig.GradleConfigSettingsPlugin"
            displayName = "Shared Gradle settings configuration"
            description = "Strict configuration cache validation."
        }
    }
}

tasks.validatePlugins {
    enableStricterValidation = true
    failOnWarning = true
}

tasks.test {
    testLogging {
        events("failed", "skipped")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
    systemProperty(
        "testFixturesDirectory",
        layout.buildDirectory.dir("test-fixtures").get().asFile.absolutePath,
    )
}

tasks.processTestResources {
    exclude("**/.gradle/**", "**/.kotlin/**", "**/build/**")
}

publishing {
    repositories {
        maven {
            name = "localTest"
            url = layout.buildDirectory.dir("repository").get().asFile.toURI()
        }
    }
}
