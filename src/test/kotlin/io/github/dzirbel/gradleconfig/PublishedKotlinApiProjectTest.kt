package io.github.dzirbel.gradleconfig

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class PublishedKotlinApiProjectTest : FixtureTest("published-kotlin-api") {
    @Test
    fun `published settings plugin works with a consumer-selected Kotlin version and a Java root`() {
        // Deliberately omit withPluginClasspath: resolve the real publication and its runtime dependencies.
        val runner = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments(
                "compileJava", ":kotlin:compileKotlin",
                "-PgradleConfigVersion=${System.getProperty("pluginVersion")}",
                "--stacktrace", "--warning-mode=fail", "--max-workers=2",
            )
        val first = runner.build()
        assertEquals(TaskOutcome.SUCCESS, first.task(":compileJava")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, first.task(":kotlin:compileKotlin")?.outcome)
        assertEquals(69, projectDir.resolve("build/classes/java/main/JavaExample.class").classVersion())
        assertEquals(69, projectDir.resolve("kotlin/build/classes/kotlin/main/FeaturesKt.class").classVersion())
        assertContains(first.output, "Configuration cache entry stored")
        assertContains(runner.build().output, "Reusing configuration cache")
    }
}
