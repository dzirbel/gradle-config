package io.github.dzirbel.gradleconfig

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register

internal class GradleProjectConfigPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val config = project.extensions.create<GradleConfigExtension>("gradleConfig")
        config.javaLanguageVersion.convention(25)
        project.pluginManager.withPlugin("java-base") {
            project.extensions.configure<JavaPluginExtension> {
                toolchain.languageVersion.convention(config.javaLanguageVersion.map(JavaLanguageVersion::of))
            }
        }
        project.pluginManager.apply("base") // TODO necessary?

        if (project.path == ":") {
            val verification = project.tasks.register<CheckGradleConfiguration>("checkGradleConfiguration") {
                group = "verification"
                description = "Checks effective Gradle options without changing the consuming build."
                kotlinDslWarningsAsErrors.convention(
                    project.providers.gradleProperty("org.gradle.kotlin.dsl.allWarningsAsErrors").orElse("false"),
                )
            }
            project.tasks.named("check") { dependsOn(verification) }
        }
    }
}
