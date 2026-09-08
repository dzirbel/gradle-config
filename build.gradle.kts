import org.gradle.buildconfiguration.tasks.UpdateDaemonJvm

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

// Generate daemon criteria from the same toolchain used to build the plugin.
tasks.named<UpdateDaemonJvm>("updateDaemonJvm") {
    languageVersion = java.toolchain.languageVersion
}

dependencies {
    implementation(libs.gradle.foojay.plugin)
    testImplementation(kotlin("test"))
}

gradlePlugin {
    plugins {
        register("gradleConfig") {
            id = "io.github.dzirbel.gradle-config"
            implementationClass = "io.github.dzirbel.gradleconfig.GradleConfigPlugin"
            displayName = "Shared Gradle settings configuration"
            description = "Opinionated Gradle settings plugin which enforces strict and bleeding-edge Gradle configuration options to the entire project."
        }
    }
}

tasks.validatePlugins {
    enableStricterValidation = true
    failOnWarning = true
}

tasks.test {
    for (version in listOf(21, 25)) {
        systemProperty(
            "jdk${version}Home",
            javaToolchains.launcherFor { languageVersion = JavaLanguageVersion.of(version) }
                .get().metadata.installationPath.asFile.absolutePath,
        )
    }
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
