package io.github.dzirbel.gradleconfig

import java.util.Properties
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class DaemonJvmProjectTest : FixtureTest("daemon-jvm") {
    @Test
    fun `generation uses root convention and supports project and CLI overrides`() {
        runner("help").build()
        assertFalse(projectDir.resolve("gradle/gradle-daemon-jvm.properties").exists())

        runner("updateDaemonJvm").build()
        assertVersion("25") // The child project's Java 21 convention must not affect the daemon.

        runner("updateDaemonJvm", "-PjavaVersion=21").build()
        assertVersion("21") // Replaces existing Java 25 criteria.

        runner("updateDaemonJvm", "-PjavaVersion=25", "--jvm-version=21").build()
        assertVersion("21") // An explicit Gradle task option takes precedence.

        runner("updateDaemonJvm", "-PjavaVersion=21", "-PdaemonVersion=25").build()
        assertVersion("25") // Explicit task DSL configuration also takes precedence.
    }

    @Test
    fun `generated criteria take precedence over Java home without changing the required version`() {
        runner("updateDaemonJvm").build()
        assertVersion("25")

        val java21 = "-Dorg.gradle.java.home=${System.getProperty("jdk21Home")}"
        val matched = runner("check", java21).build()
        assertContains(matched.output, "Gradle JVM=25 [OK]")

        // Changing the extension does not refresh existing criteria or change the running daemon.
        val stale = runner("check", java21, "-PjavaVersion=21").buildAndFail()
        assertContains(stale.output, "Gradle JVM=25; required: Gradle JVM=21")
        assertContains(stale.output, "./gradlew updateDaemonJvm")
        assertVersion("25")

        runner("updateDaemonJvm", "-PjavaVersion=21").build()
        val refreshed = runner("check", java21, "-PjavaVersion=21").build()
        assertContains(refreshed.output, "Gradle JVM=21 [OK]")
    }

    @Test
    fun `criteria generation supports configuration cache reuse`() {
        val tasks = arrayOf("updateDaemonJvm", "-PjavaVersion=21")
        runner(*tasks).build()
        // The new criteria file can invalidate configuration recorded before it existed.
        runner(*tasks).build()
        val cached = runner(*tasks).build()
        assertContains(cached.output, "Reusing configuration cache")
        assertVersion("21")
    }

    @Test
    fun `running JVM must match root version even with configuration cache reuse`() {
        val java21 = "-Dorg.gradle.java.home=${System.getProperty("jdk21Home")}"
        val java25 = "-Dorg.gradle.java.home=${System.getProperty("jdk25Home")}"

        // Default root version is 25, despite the child project requesting 21.
        val matched = runner("check", java25).build()
        assertContains(matched.output, "Gradle JVM=25 [OK]")
        val cachedMatch = runner("check", java25).build()
        assertContains(cachedMatch.output, "Reusing configuration cache")
        assertContains(cachedMatch.output, "Gradle JVM=25 [OK]")

        val mismatch = runner("check", java21).buildAndFail()
        assertContains(mismatch.output, "Gradle JVM=21; required: Gradle JVM=25")
        assertContains(mismatch.output, "1 required setting(s) to fix")
        assertContains(mismatch.output, "JAVA_HOME")
        val cachedMismatch = runner("check", java21).buildAndFail()
        assertContains(cachedMismatch.output, "Reusing configuration cache")
        assertContains(cachedMismatch.output, "Gradle JVM=21; required: Gradle JVM=25")

        val override = runner("check", java21, "-PjavaVersion=21").build()
        assertContains(override.output, "Gradle JVM=21 [OK]")
        // A newer daemon is also a mismatch: require equality, not a minimum version.
        val newer = runner("check", java25, "-PjavaVersion=21").buildAndFail()
        assertContains(newer.output, "Gradle JVM=25; required: Gradle JVM=21")
        assertFalse(projectDir.resolve("gradle/gradle-daemon-jvm.properties").exists())
    }

    @Test
    fun `warn only reports JVM mismatch without failing`() {
        val result = runner(
            "checkGradleConfiguration", "--warn-only",
            "-Dorg.gradle.java.home=${System.getProperty("jdk21Home")}",
        ).build()
        assertContains(result.output, "Gradle JVM=21; required: Gradle JVM=25")
    }

    private fun assertVersion(expected: String) {
        val properties = Properties().apply {
            projectDir.resolve("gradle/gradle-daemon-jvm.properties").inputStream().use(::load)
        }
        assertEquals(expected, properties.getProperty("toolchainVersion"))
    }
}
