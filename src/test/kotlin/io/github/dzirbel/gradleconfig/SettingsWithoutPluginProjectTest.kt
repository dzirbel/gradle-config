package io.github.dzirbel.gradleconfig

import kotlin.test.Test

class SettingsWithoutPluginProjectTest : FixtureTest("settings-without-plugin") {
    @Test
    fun `task execution listeners are accepted without strict mode`() {
        runner(
            "verify",
            "--no-configuration-cache",
            "--no-isolated-projects",
        ).build()
    }
}
