package io.github.dzirbel.gradleconfig

import org.gradle.testkit.runner.TaskOutcome
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class DependenciesProjectTest : FixtureTest("dependencies") {
    @Test
    fun `aligned dependencies resolve in every project and reuse configuration cache`() {
        val tasks = listOf(":resolveAligned", ":child:resolveAligned")
        val first = runner(tasks).build()
        val second = runner(tasks).build()
        for (result in listOf(first, second)) {
            tasks.forEach { assertEquals(TaskOutcome.SUCCESS, result.task(it)?.outcome) }
            assertContains(result.output, "Resolved dependency graph")
        }
        assertContains(second.output, "Reusing configuration cache")
    }

    @Test
    fun `transitive dependency cannot silently upgrade a direct dependency`() {
        for (prefix in listOf(":", ":child:")) {
            val result = runner("${prefix}resolveConflict").buildAndFail()
            assertContains(result.output, "test:leaf")
            assertContains(result.output, "2.0")
            assertContains(result.output, "1.0")
            assertContains(result.output, "Conflict found for module")
        }
    }

    @Test
    fun `conflicts between transitive dependencies also fail`() {
        val result = runner("resolveTransitiveConflict").buildAndFail()
        assertContains(result.output, "test:leaf")
        assertContains(result.output, "Conflict found for module")
    }

    @Test
    fun `dynamic versions fail even in transitive dependencies`() {
        for (task in listOf("resolveDynamic", "resolveTransitiveDynamic")) {
            val result = runner(task).buildAndFail()
            assertContains(result.output, "test:leaf:2.+")
            assertContains(result.output, "Resolution strategy disallows usage of dynamic versions")
        }
    }

    @Test
    fun `changing modules and transitive snapshots fail`() {
        for (task in listOf("resolveChanging", "resolveTransitiveChanging")) {
            val result = runner(task).buildAndFail()
            assertContains(result.output, "test:leaf")
            assertContains(result.output, "Resolution strategy disallows usage of changing versions")
        }
    }
}
