package io.github.dzirbel.gradleconfig

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register

class GradleConfigPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        requireSupportedGradle()
        val config = project.extensions.create<GradleConfigExtension>("gradleConfig")
        config.javaLanguageVersion.convention(25)

        project.pluginManager.withPlugin("java-base") {
            project.extensions.configure<JavaPluginExtension> {
                toolchain.languageVersion.convention(config.javaLanguageVersion.map(JavaLanguageVersion::of))
            }
        }

        project.pluginManager.apply("base")

        project.tasks.register<GenerateGradleProperties>("generateGradleProperties") {
            group = "build setup"
            description = "Generates recommended startup properties for adoption in the build's gradle.properties."
            contents.convention(
                GradleConfigPlugin::class.java.getResource("/io/github/dzirbel/gradleconfig/gradle.properties")!!.readText(),
            )
            outputFile.convention(project.layout.buildDirectory.file("gradle-config/gradle.properties"))
        }
    }
}
