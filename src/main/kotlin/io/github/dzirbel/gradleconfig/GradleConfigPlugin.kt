package io.github.dzirbel.gradleconfig

import com.autonomousapps.DependencyAnalysisExtension
import org.gradle.api.GradleException
import org.gradle.api.IsolatedAction
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.api.initialization.resolve.RepositoriesMode
import org.gradle.kotlin.dsl.configure
import org.gradle.util.GradleVersion

class GradleConfigPlugin : Plugin<Settings> {
    override fun apply(settings: Settings) {
        if (GradleVersion.current() < GradleVersion.version("9.7.1")) {
            throw GradleException("gradle-config requires Gradle 9.7.1 or newer. Upgrade the consuming build's wrapper.")
        }

        settings.enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

        settings.pluginManagement.repositories { gradlePluginPortal() }
        settings.pluginManager.apply("org.gradle.toolchains.foojay-resolver-convention")
        // Consumers must load Kotlin/Android plugins in settings with apply false so analysis can see their classes.
        settings.pluginManager.apply("com.autonomousapps.build-health")
        settings.extensions.configure<DependencyAnalysisExtension> {
            reporting { printBuildHealth(true) }
            issues {
                all {
                    onAny { severity("fail") }
                    // Duplicate-class warnings have their own severity, independent of onAny.
                    onDuplicateClassWarnings { severity("fail") }
                }
            }
        }

        settings.dependencyResolutionManagement {
            repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
            repositories {
                google()
                mavenCentral()
            }
        }

        settings.gradle.lifecycle.beforeProject(ConfigureProject())

        // TODO licenses?
    }
}

private class ConfigureProject : IsolatedAction<Project> {
    override fun execute(target: Project) {
        target.pluginManager.apply(GradleProjectConfigPlugin::class.java)
    }
}
