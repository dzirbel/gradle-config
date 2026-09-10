package io.github.dzirbel.gradleconfig

import org.gradle.testkit.runner.TaskOutcome
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KotlinPolicyProjectTest : FixtureTest("kotlin-policy") {
    @Test
    fun `experimental language features compile and run with strict defaults and configuration cache`() {
        val first = runner(":valid:test", ":override:compileKotlin").build()
        assertEquals(TaskOutcome.SUCCESS, first.task(":valid:test")?.outcome)
        assertContains(first.output, "Configuration cache entry stored")
        val report = projectDir.resolve("valid/build/test-results/test/TEST-FeaturesTest.xml").readText()
        assertContains(report, "tests=\"1\"")
        assertContains(report, "failures=\"0\"")
        assertTrue(projectDir.resolve("valid/build/classes/kotlin/main/Greeting.class").isFile)
        assertFalse(projectDir.resolve("valid/build/classes/kotlin/main/Greeting\$DefaultImpls.class").exists())

        val second = runner(":valid:test", ":override:compileKotlin").build()
        assertContains(second.output, "Reusing configuration cache")
    }

    @Test
    fun `compiler accepts style issues but rejects ordinary warnings nullability violations and missing opt-ins`() {
        val result = runner(
            ":warning:compileKotlin",
            ":extra-warning:compileKotlin",
            ":unused-result:compileKotlin",
            ":nullability:compileKotlin",
            ":opt-in:compileKotlin",
            "--continue",
        ).buildAndFail()
        for (module in listOf("extra-warning", "unused-result")) {
            assertEquals(TaskOutcome.SUCCESS, result.task(":$module:compileKotlin")?.outcome, module)
        }
        for (module in listOf("warning", "nullability", "opt-in")) {
            assertEquals(TaskOutcome.FAILED, result.task(":$module:compileKotlin")?.outcome, module)
        }
        assertContains(result.output, "[DEPRECATION]")
        assertContains(result.output, "[NULL_FOR_NONNULL_TYPE]")
        assertContains(result.output, "[OPT_IN_USAGE_ERROR]")
    }
}
