package io.github.dzirbel.gradleconfig

import org.gradle.testkit.runner.TaskOutcome
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class KotlinMultiplatformProjectTest : FixtureTest("kotlin-multiplatform") {
    @Test
    fun `common policy compiles JVM JS Wasm and Native and runs JVM tests`() {
        val tasks = listOf("jvmTest", "compileKotlinJs", "compileKotlinWasmJs", "compileKotlinLinuxX64")
        val result = runner(tasks).build()
        for (task in listOf(":jvmTest", ":compileKotlinJs", ":compileKotlinWasmJs", ":compileKotlinLinuxX64")) {
            assertEquals(TaskOutcome.SUCCESS, result.task(task)?.outcome, task)
        }
        assertEquals(69, projectDir.resolve("build/classes/kotlin/jvm/main/Greeter.class").classVersion())
        assertFalse(projectDir.resolve("build/classes/kotlin/jvm/main/Greeting\$DefaultImpls.class").exists())
        assertContains(result.output, "Configuration cache entry stored")
        assertContains(runner(tasks).build().output, "Reusing configuration cache")
    }
}
