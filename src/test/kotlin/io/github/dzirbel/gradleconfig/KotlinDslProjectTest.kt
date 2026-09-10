package io.github.dzirbel.gradleconfig

import org.gradle.testkit.runner.TaskOutcome
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class KotlinDslProjectTest : FixtureTest("kotlin-dsl") {
    @Test
    fun `Kotlin DSL projects compile with Gradle embedded Kotlin`() {
        val first = runner("compileKotlin").build()
        assertEquals(TaskOutcome.SUCCESS, first.task(":compileKotlin")?.outcome)
        assertContains(first.output, "Configuration cache entry stored")
        assertContains(runner("compileKotlin").build().output, "Reusing configuration cache")
    }
}
