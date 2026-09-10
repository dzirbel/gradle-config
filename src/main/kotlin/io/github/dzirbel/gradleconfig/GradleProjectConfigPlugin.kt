package io.github.dzirbel.gradleconfig

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.buildconfiguration.tasks.UpdateDaemonJvm
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withGroovyBuilder
import org.gradle.kotlin.dsl.withType

internal class GradleProjectConfigPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val config = project.extensions.create<GradleConfigExtension>("gradleConfig")
        config.javaLanguageVersion.convention(25)

        // Kotlin/JVM also uses this Java toolchain and derives its default jvmTarget from it.
        // Keep the provider lazy so project overrides apply to both languages.
        project.pluginManager.withPlugin("java-base") {
            project.extensions.configure<JavaPluginExtension> {
                toolchain.languageVersion.set(config.javaLanguageVersion.map(JavaLanguageVersion::of))
            }
        }

        project.configureKotlin(config)

        project.pluginManager.withPlugin("com.google.devtools.ksp") {
            // KSP has no separate Gradle API artifact; avoid bundling its plugin implementation.
            project.extensions.getByName("ksp").withGroovyBuilder {
                setProperty("allWarningsAsErrors", true)
            }
        }

        // TODO resolutionStrategy.failOnNonReproducibleResolution()
        // TODO configure test logging (and reports?)
        //  - fail on writing to std_out or std_err
        // TODO task configuration:
        //  - check / test / compile
        //  - Android default variant

        if (project.path == ":") {
            project.pluginManager.apply("base")

            project.tasks.withType<UpdateDaemonJvm>().configureEach {
                languageVersion.set(config.javaLanguageVersion.map(JavaLanguageVersion::of))
            }

            val verification = project.tasks.register<CheckGradleConfiguration>("checkGradleConfiguration") {
                javaLanguageVersion.convention(config.javaLanguageVersion)
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
