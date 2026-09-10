package io.github.dzirbel.gradleconfig

import org.gradle.testkit.runner.TaskOutcome
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class KspProjectTest : FixtureTest("ksp") {
    @Test
    fun `processor warnings fail by default but can be relaxed with configuration cache reuse`() {
        val tasks = listOf(":strict:kspKotlin", ":relaxed:kspKotlin", "--continue")
        val first = runner(tasks).buildAndFail()
        assertEquals(TaskOutcome.FAILED, first.task(":strict:kspKotlin")?.outcome, first.output)
        assertEquals(TaskOutcome.SUCCESS, first.task(":relaxed:kspKotlin")?.outcome, first.output)
        assertContains(first.output, "Fixture processor warning")
        assertContains(first.output, "Configuration cache entry stored")

        val second = runner(tasks).buildAndFail()
        assertEquals(TaskOutcome.FAILED, second.task(":strict:kspKotlin")?.outcome, second.output)
        assertEquals(TaskOutcome.UP_TO_DATE, second.task(":relaxed:kspKotlin")?.outcome, second.output)
        assertContains(second.output, "Fixture processor warning")
        assertContains(second.output, "Reusing configuration cache")
    }
}
