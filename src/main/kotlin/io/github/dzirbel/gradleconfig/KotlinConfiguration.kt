package io.github.dzirbel.gradleconfig

import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.HasConfigurableKotlinCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.JvmDefaultMode
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.plugin.KotlinTargetsContainer
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

internal fun Project.configureKotlin(config: GradleConfigExtension) {
    var configured = false
    for (id in kotlinPlugins) {
        pluginManager.withPlugin(id) {
            val kotlin = extensions.findByType<KotlinBaseExtension>() ?: return@withPlugin

            if (!configured) {
                configured = true
                kotlin as HasConfigurableKotlinCompilerOptions<*>

                val compilerOptions = kotlin.compilerOptions
                compilerOptions.allWarningsAsErrors.set(true)
                compilerOptions.progressiveMode.set(true)
                compilerOptions.languageVersion.set(KotlinVersion.KOTLIN_2_4)
                compilerOptions.apiVersion.set(KotlinVersion.KOTLIN_2_4)

                compilerOptions.freeCompilerArgs.addAll(
                    "-Xreport-all-warnings",
                    "-Xrender-internal-diagnostic-names",
                    "-Xexplicit-context-arguments",
                    "-Xcontext-sensitive-resolution",
                    "-Xcollection-literals",
                    "-Xname-based-destructuring=complete",
                    "-Xlocal-type-aliases",
                    "-Xintrinsic-const-evaluation",
                )

                configureJvmOptions(compilerOptions)
                if (kotlin is KotlinTargetsContainer) {
                    kotlin.targets.configureEach {
                        if (this is HasConfigurableKotlinCompilerOptions<*>) configureJvmOptions(this.compilerOptions)
                    }
                }

                kotlin.jvmToolchain {
                    languageVersion.set(config.javaLanguageVersion.map(JavaLanguageVersion::of))
                }
            }
        }
    }

    pluginManager.withPlugin("org.gradle.kotlin.kotlin-dsl") {
        val kotlin = extensions.getByName("kotlin") as HasConfigurableKotlinCompilerOptions<*>
        val defaults = kotlin.compilerOptions
        // Kotlin DSL pins task-level language/API versions (2.2 in Gradle 9.7.1), overriding KGP's
        // extension inheritance. Restore that inheritance after Kotlin DSL has installed its defaults.
        tasks.withType<KotlinCompilationTask<*>>().configureEach {
            if (compilerOptions is KotlinJvmCompilerOptions) {
                compilerOptions.languageVersion.set(defaults.languageVersion)
                compilerOptions.apiVersion.set(defaults.apiVersion)
            }
        }
    }
}

private val kotlinPlugins = listOf(
    "org.jetbrains.kotlin.jvm",
    "org.jetbrains.kotlin.multiplatform",
    "org.jetbrains.kotlin.android",
    // AGP's built-in Kotlin exposes the extension without applying kotlin-android.
    "com.android.application",
    "com.android.library",
    "com.android.test",
    "com.android.dynamic-feature",
)

private fun configureJvmOptions(options: KotlinCommonCompilerOptions) {
    if (options !is KotlinJvmCompilerOptions) return
    options.javaParameters.set(true)
    options.jvmDefault.set(JvmDefaultMode.NO_COMPATIBILITY)
    options.freeCompilerArgs.addAll(
        "-Xjsr305=strict",
        "-Xjsr305=under-migration:strict",
        "-Xemit-jvm-type-annotations",
        "-Xassertions=always-enable",
    )
}
