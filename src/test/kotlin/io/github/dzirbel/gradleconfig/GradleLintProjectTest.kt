package io.github.dzirbel.gradleconfig

import org.gradle.testkit.runner.TaskOutcome
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GradleLintProjectTest : FixtureTest("gradle-lint") {
    @Test
    fun `check rejects unsorted dependencies and unformatted Gradle scripts without editing them`() {
        val scripts = projectDir.walkTopDown().filter { it.isFile }.associate { it.relativeTo(projectDir) to it.readText() }
        val result = runner("check", "--continue").buildAndFail()

        for (path in listOf("", ":kotlin", ":groovy")) {
            assertEquals(TaskOutcome.FAILED, result.task("$path:checkSortDependencies")?.outcome, result.output)
        }
        assertEquals(TaskOutcome.FAILED, result.task(":spotlessKotlinGradleCheck")?.outcome, result.output)
        assertEquals(TaskOutcome.FAILED, result.task(":kotlin:spotlessKotlinGradleCheck")?.outcome, result.output)
        assertContains(result.output, "settings.gradle.kts")
        assertContains(result.output, "gradle/shared.gradle.kts")
        assertContains(result.output, "src/main/kotlin/convention.gradle.kts")
        for ((path, content) in scripts) {
            assertEquals(content, projectDir.resolve(path).readText(), "$path was changed by check")
        }
    }

    @Test
    fun `automatic fixes pass check and reuse configuration cache`() {
        val excludedPaths = listOf("kotlin/src/main/kotlin/Source.kt", "kotlin/src/test/resources/example.gradle.kts")
        val excluded = excludedPaths.associateWith { projectDir.resolve(it).readText() }
        val fixed = runner("sortDependencies", "spotlessApply").build()
        for (path in listOf("", ":kotlin", ":groovy")) {
            assertEquals(TaskOutcome.SUCCESS, fixed.task("$path:sortDependencies")?.outcome, fixed.output)
        }
        assertNull(fixed.task(":container:sortDependencies"))

        for (path in listOf("build.gradle.kts", "kotlin/build.gradle.kts", "groovy/build.gradle")) {
            val expected = requireNotNull(javaClass.getResource("/gradle-lint-expected/$path")).readText()
            assertEquals(expected, projectDir.resolve(path).readText(), path)
        }
        for ((path, content) in excluded) {
            assertEquals(content, projectDir.resolve(path).readText(), path)
        }

        val first = runner("check", "--rerun-tasks").build()
        for (path in listOf("", ":kotlin", ":groovy")) {
            assertEquals(TaskOutcome.SUCCESS, first.task("$path:checkSortDependencies")?.outcome, first.output)
        }
        assertTrue(first.tasks.any { it.path == ":container:leaf:spotlessKotlinGradleCheck" })
        val cached = runner("check", "--rerun-tasks").build()
        assertContains(cached.output, "Reusing configuration cache")
        assertEquals(TaskOutcome.SUCCESS, cached.task(":kotlin:checkSortDependencies")?.outcome, cached.output)
    }

    @Test
    fun `subproject check enforces dependency sorting`() {
        val result = runner(":kotlin:check", "--continue").buildAndFail()
        assertEquals(TaskOutcome.FAILED, result.task(":kotlin:checkSortDependencies")?.outcome, result.output)
        assertNull(result.task(":checkSortDependencies"))
    }
}
