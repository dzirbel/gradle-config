package io.github.dzirbel.gradleconfig

import org.gradle.testkit.runner.TaskOutcome
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class PropertiesProjectTest : FixtureTest("properties") {
    @Test
    fun `checks the build without changing any source files and reuses configuration cache`() {
        val sources = projectDir.walkTopDown().filter { it.isFile }.associate { it.relativeTo(projectDir) to it.readText() }
        val first = runner("check").build()
        assertEquals(TaskOutcome.SUCCESS, first.task(":checkGradleConfiguration")?.outcome)
        assertContains(first.output, "org.gradle.configuration-cache=true [OK]")
        assertContains(first.output, "org.gradle.parallel=true [OK]")
        val second = runner("check").build()
        assertContains(second.output, "Reusing configuration cache")
        assertEquals(TaskOutcome.SUCCESS, second.task(":checkGradleConfiguration")?.outcome)
        sources.forEach { (path, contents) -> assertEquals(contents, projectDir.resolve(path).readText()) }
        assertFalse(projectDir.resolve("build/gradle-config/gradle.properties").exists())
    }

    @Test
    fun `explicit disabled flags and project property overrides are preserved and rejected`() {
        val failure = runner(
            "checkGradleConfiguration",
            "--no-configuration-cache",
            "--no-isolated-projects",
            "--no-build-cache",
            "--no-parallel",
            "--warning-mode=all",
            "-Porg.gradle.kotlin.dsl.allWarningsAsErrors=false",
        ).buildAndFail()
        assertContains(failure.output, "org.gradle.configuration-cache=false; required")
        assertContains(failure.output, "org.gradle.isolated-projects=false; recommended")
        assertContains(failure.output, "org.gradle.caching=false; required")
        assertContains(failure.output, "org.gradle.parallel=false; required")
        assertContains(failure.output, "org.gradle.warning.mode=all; required")
        assertContains(failure.output, "org.gradle.kotlin.dsl.allWarningsAsErrors=false; required")
    }

    @Test
    fun `warn only reports required differences without changing them`() {
        val result = runner("checkGradleConfiguration", "--warn-only", "--warning-mode=all").build()
        assertContains(result.output, "org.gradle.warning.mode=all; required")
    }

    @Test
    fun `recommendations warn without failing`() {
        val result = runner("checkGradleConfiguration").build()
        assertContains(result.output, "org.gradle.isolated-projects=false; recommended")
        assertContains(result.output, "org.gradle.configuration-cache.parallel=false; recommended")
    }

    @Test
    fun `reads current invocation options even when cached task state is reused`() {
        runner("checkGradleConfiguration", "--warn-only").build()
        val changed = runner("checkGradleConfiguration", "--warn-only", "--no-build-cache", "--warning-mode=all").build()
        assertContains(changed.output, "org.gradle.caching=false; required")
        assertContains(changed.output, "org.gradle.warning.mode=all; required")
        val repeated = runner("checkGradleConfiguration", "--warn-only", "--no-build-cache", "--warning-mode=all").build()
        assertContains(repeated.output, "Reusing configuration cache")
        assertContains(repeated.output, "org.gradle.caching=false; required")
    }

    @Test
    fun `each required setting independently fails verification`() {
        val overrides = mapOf(
            "--no-configuration-cache" to "org.gradle.configuration-cache=false",
            "--no-build-cache" to "org.gradle.caching=false",
            "--no-parallel" to "org.gradle.parallel=false",
            "--warning-mode=all" to "org.gradle.warning.mode=all",
            "-Porg.gradle.kotlin.dsl.allWarningsAsErrors=false" to "org.gradle.kotlin.dsl.allWarningsAsErrors=false",
            "--configuration-cache-problems=warn" to "org.gradle.configuration-cache.problems=warn",
        )
        overrides.forEach { (argument, expected) ->
            val failure = runner("checkGradleConfiguration", argument).buildAndFail()
            assertContains(failure.output, "$expected; required")
            assertContains(failure.output, "1 required setting(s) to fix")
        }
    }

    @Test
    fun `parallel execution remains required even with isolation active`() {
        val result = runner(
            "checkGradleConfiguration",
            "--isolated-projects",
            "--no-parallel",
            "-Dorg.gradle.configuration-cache.parallel=false",
        ).buildAndFail()
        assertContains(result.output, "org.gradle.parallel=false; required")
        assertContains(result.output, "org.gradle.configuration-cache.parallel=true [OK]")
        assertContains(result.output, "1 required setting(s) to fix")
    }

    @Test
    fun `isolation diagnostics disables implied parallel configuration caching`() {
        val result = runner(
            "checkGradleConfiguration",
            "--isolated-projects",
            "-Dorg.gradle.isolated-projects.diagnostics=true",
        ).build()
        assertContains(result.output, "org.gradle.configuration-cache.parallel=false; recommended")
    }

    @Test
    fun `problem policy is checked even when configuration cache is disabled`() {
        val result = runner(
            "checkGradleConfiguration",
            "--no-configuration-cache",
            "--configuration-cache-problems=warn",
        ).buildAndFail()
        assertContains(result.output, "org.gradle.configuration-cache=false; required")
        assertContains(result.output, "org.gradle.configuration-cache.problems=warn; required")
        assertContains(result.output, "2 required setting(s) to fix")
    }
}
