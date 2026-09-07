package io.github.dzirbel.gradleconfig

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse

class DefaultsProjectTest : FixtureTest("defaults") {
    @Test
    fun `strict mode accepts a build without task execution listeners when caching is disabled`() {
        runner(
            "help",
            "--no-configuration-cache",
            "--no-isolated-projects",
        ).build()
    }

    @Test
    fun `reports missing startup configuration without creating properties`() {
        val result = runner(
            "checkGradleConfiguration",
            "--no-configuration-cache",
            "--no-isolated-projects",
            "--warning-mode=all",
        ).buildAndFail()
        assertContains(result.output, "org.gradle.kotlin.dsl.allWarningsAsErrors=false; required")
        assertContains(result.output, "org.gradle.configuration-cache=false; required")
        assertFalse(projectDir.resolve("gradle.properties").exists())
    }
}
