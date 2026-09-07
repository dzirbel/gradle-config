package io.github.dzirbel.gradleconfig

import org.gradle.StartParameter
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.configuration.BuildFeatures
import org.gradle.api.internal.StartParameterInternal
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.work.DisableCachingByDefault
import javax.inject.Inject

/** Checks the current invocation, including on a configuration-cache hit. Never mutates options. */
@DisableCachingByDefault(because = "Reports invocation state on every execution; has no outputs")
abstract class CheckGradleConfiguration : DefaultTask() {
    @get:Inject
    protected abstract val startParameter: StartParameter

    @get:Inject
    protected abstract val buildFeatures: BuildFeatures

    @get:Input
    abstract val kotlinDslWarningsAsErrors: Property<String>

    @get:Input
    abstract val warnOnly: Property<Boolean>

    init {
        warnOnly.convention(false)
    }

    @Option(option = "warn-only", description = "Report required settings as warnings instead of failing.")
    fun configureWarnOnly(value: Boolean) {
        warnOnly.set(value)
    }

    @TaskAction
    fun checkConfiguration() {
        val isolated = buildFeatures.isolatedProjects.active.get()
        val cached = buildFeatures.configurationCache.active.get()
        val failures = mutableListOf<String>()

        fun check(name: String, actual: String, expected: String, required: Boolean) {
            if (actual == expected) {
                logger.lifecycle("$name=$actual [OK]")
            } else {
                val severity = if (required) "required" else "recommended"
                val message = "$name=$actual; $severity: $name=$expected. Set it in the consuming build's gradle.properties or invocation."
                logger.warn(message)
                if (required) failures.add(message)
            }
        }

        check("org.gradle.configuration-cache", cached.toString(), "true", true)
        check("org.gradle.caching", startParameter.isBuildCacheEnabled.toString(), "true", true)
        check("org.gradle.warning.mode", startParameter.warningMode.name.lowercase(), "fail", true)
        check("org.gradle.kotlin.dsl.allWarningsAsErrors", kotlinDslWarningsAsErrors.get(), "true", true)
        check("org.gradle.parallel", startParameter.isParallelProjectExecutionEnabled.toString(), "true", true)

        check("org.gradle.isolated-projects", isolated.toString(), "true", false)

        // Gradle 9.7.1 has no public readers for these two resolved options. Keep this read-only compatibility boundary
        // explicit: property lookup would miss CLI overrides such as --configuration-cache-problems=warn. Never fall
        // back to reporting a guessed value.
        val options = startParameter as? StartParameterInternal
            ?: throw GradleException("Cannot inspect Gradle configuration-cache options on this Gradle version.")
        check("org.gradle.configuration-cache.problems", options.configurationCacheProblems.name.lowercase(), "fail", true)
        val parallelCache = if (isolated) !options.isIsolatedProjectsDiagnostics else options.isConfigurationCacheParallel
        check("org.gradle.configuration-cache.parallel", parallelCache.toString(), "true", false)

        if (failures.isNotEmpty() && !warnOnly.get()) {
            throw GradleException("Gradle configuration has ${failures.size} required setting(s) to fix. Use --warn-only to report without failing.")
        }
    }
}
