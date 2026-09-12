package io.github.dzirbel.gradleconfig

import org.gradle.testkit.runner.TaskOutcome
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class DependencyAnalysisProjectTest : FixtureTest("dependency-analysis") {
    @Test
    fun `check rejects dependency advice in subprojects`() {
        for ((project, advice) in listOf(
            "transitive" to "implementation(project(\":leaf\"))",
            "kotlin-transitive" to "implementation(project(\":leaf\"))",
            "unused" to "implementation(project(\":leaf\"))",
            "incorrect" to "api(project(\":leaf\"))",
            "test-unused" to "testImplementation(project(\":leaf\"))",
        )) {
            val result = runner(":$project:check").buildAndFail()
            assertEquals(TaskOutcome.FAILED, result.task(":$project:projectHealth")?.outcome, result.output)
            assertContains(result.output, advice)
        }
    }

    @Test
    fun `correct declarations pass check and reuse configuration cache`() {
        val first = runner(":valid:check").build()
        assertEquals(TaskOutcome.SUCCESS, first.task(":valid:projectHealth")?.outcome)
        val second = runner(":valid:check").build()
        assertContains(second.output, "Reusing configuration cache")
    }

    @Test
    fun `duplicate classes fail check`() {
        val result = runner(":duplicates:check").buildAndFail()
        assertEquals(TaskOutcome.FAILED, result.task(":duplicates:projectHealth")?.outcome, result.output)
        assertContains(result.output, "shared/Duplicate is provided by multiple dependencies")
    }

    @Test
    fun `root check analyzes the entire build`() {
        val result = runner(":check").buildAndFail()
        assertContains(result.output, ":buildHealth")
        assertContains(result.output, ":transitive")
        assertContains(result.output, "api(project(\":leaf\"))")
    }
}
