package io.github.dzirbel.gradleconfig

import org.gradle.testkit.runner.TaskOutcome
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class TestLoggingProjectTest : FixtureTest("test-logging") {
    @Test
    fun `passing and skipped tests stay quiet`() {
        val result = runner("test", "--rerun-tasks").build()
        assertEquals(TaskOutcome.SUCCESS, result.task(":test")?.outcome)
        assertFalse(result.output.contains("PASSED"))
        assertFalse(result.output.contains("SKIPPED"))
        assertQuietStreams(result.output)

        val report = projectDir.resolve("build/test-results/test/TEST-QuietTest.xml").readText()
        assertContains(report, "tests=\"2\"")
        assertContains(report, "skipped=\"1\"")
        assertContains(report, "failures=\"0\"")
    }

    @Test
    fun `custom test task logs full failures and causes with configuration cache reuse`() {
        repeat(2) { run ->
            val result = runner("failingTest").buildAndFail()
            assertEquals(TaskOutcome.FAILED, result.task(":failingTest")?.outcome)
            assertContains(result.output, "FailingTest > failing FAILED")
            assertContains(result.output, "java.lang.IllegalStateException: outer-failure-marker")
            assertContains(result.output, "at FailingTest.failing(FailingTest.java:")
            assertContains(result.output, "Caused by:")
            assertContains(result.output, "java.lang.IllegalArgumentException: inner-failure-marker")
            assertContains(result.output, "at FailingTest.failWithCause(FailingTest.java:")
            // A framework frame proves the trace is not truncated at the test method.
            assertContains(result.output, "at org.junit.runners.model.FrameworkMethod")
            assertQuietStreams(result.output)
            if (run == 1) assertContains(result.output, "Reusing configuration cache")
        }
    }

    @Test
    fun `stdout stderr and suite output fail otherwise passing tests and retain reports`() {
        val cases = listOf(
            Triple("OutputTest.stdout", "StdOut", "test-stdout-marker"),
            Triple("OutputTest.stderr", "StdErr", "test-stderr-marker"),
            Triple("SetupOutputTest", "StdErr", "test-setup-marker"),
        )
        for ((test, destination, marker) in cases) {
            repeat(2) { run ->
                val result = runner("outputTest", "--tests", test).buildAndFail()
                assertEquals(TaskOutcome.FAILED, result.task(":outputTest")?.outcome)
                assertContains(result.output, "Tests must not write to stdout or stderr.")
                assertContains(result.output, "wrote to $destination:")
                assertContains(result.output, marker)
                val report = projectDir.resolve(
                    "build/test-results/outputTest/TEST-${test.substringBefore('.')}.xml",
                ).readText()
                assertContains(report, "tests=\"1\"")
                assertContains(report, "failures=\"0\"")
                assertContains(report, marker)
                if (run == 1) assertContains(result.output, "Reusing configuration cache")
            }
        }
    }

    private fun assertQuietStreams(output: String) {
        assertFalse(output.contains("test-stdout-marker"))
        assertFalse(output.contains("test-stderr-marker"))
    }
}
