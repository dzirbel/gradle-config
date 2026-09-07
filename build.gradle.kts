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
            displayName = "Shared Gradle settings configuration"
            description = "JVM toolchain conventions, strict validation, and consuming-build configuration checks."
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
