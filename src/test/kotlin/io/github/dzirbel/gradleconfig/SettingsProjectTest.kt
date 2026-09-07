package io.github.dzirbel.gradleconfig

import kotlin.test.Test
import kotlin.test.assertContains

class SettingsProjectTest : FixtureTest("settings") {
    @Test
    fun `strict mode rejects task execution listeners even without configuration cache`() {
        val failure = runner(
            "verify",
            "--no-configuration-cache",
            "--no-isolated-projects",
        ).buildAndFail()
        assertContains(failure.output, "Listener registration using Gradle.addListener")

        runner(
            "verify",
            "-Pcompatible=true",
            "--no-configuration-cache",
            "--no-isolated-projects",
        ).build()

        runner(
            "verify",
            "-Prelaxed=true",
            "--no-configuration-cache",
            "--no-isolated-projects",
        ).build()
    }
}
