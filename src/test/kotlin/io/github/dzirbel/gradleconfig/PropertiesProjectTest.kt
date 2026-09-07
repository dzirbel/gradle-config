package io.github.dzirbel.gradleconfig

import org.gradle.testkit.runner.TaskOutcome
import java.util.Properties
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PropertiesProjectTest : FixtureTest("properties") {
    @Test
    fun `generates startup settings without changing source and reuses task and configuration caches`() {
        val source = projectDir.resolve("gradle.properties")
        val original = source.readText()
        val first = runner("generateGradleProperties", "--build-cache").build()
        assertEquals(TaskOutcome.SUCCESS, first.task(":generateGradleProperties")?.outcome)
        val output = projectDir.resolve("build/gradle-config/gradle.properties")
        val generated = output.readText()
        val properties = Properties().apply { generated.reader().use { load(it) } }
        val expected = mapOf(
            "org.gradle.caching" to "true",
            "org.gradle.parallel" to "true",
            "org.gradle.configuration-cache" to "true",
            "org.gradle.configuration-cache.problems" to "fail",
            "org.gradle.configuration-cache.parallel" to "true",
            "org.gradle.isolated-projects" to "true",
            "org.gradle.warning.mode" to "fail",
            "org.gradle.kotlin.dsl.allWarningsAsErrors" to "true",
        )
        assertEquals(expected, properties.entries.associate { it.key.toString() to it.value.toString() })
        assertEquals(original, source.readText())

        val second = runner("generateGradleProperties", "--build-cache").build()
        assertEquals(TaskOutcome.UP_TO_DATE, second.task(":generateGradleProperties")?.outcome)
        assertContains(second.output, "Reusing configuration cache")

        assertTrue(output.delete())
        val restored = runner("generateGradleProperties", "--build-cache").build()
        assertEquals(TaskOutcome.FROM_CACHE, restored.task(":generateGradleProperties")?.outcome)
        assertEquals(generated, output.readText())
        assertEquals(original, source.readText())
    }
}
