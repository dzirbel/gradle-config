package io.github.dzirbel.gradleconfig

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class JvmProjectTest : FixtureTest("jvm") {
    @Test
    fun `settings plugin configures all JVM projects and respects overrides under isolation`() {
        val tasks = listOf(":default:compileJava", ":override:compileJava", ":explicit:compileJava")
        val first = runner(tasks).build()
        assertContains(first.output, "Configuration cache entry stored")

        assertEquals(69, projectDir.resolve("default/build/classes/java/main/Example.class").classVersion()) // Java 25
        assertEquals(65, projectDir.resolve("override/build/classes/java/main/Example.class").classVersion()) // Java 21 via gradleConfig
        assertEquals(65, projectDir.resolve("explicit/build/classes/java/main/Example.class").classVersion()) // Java 21 via java.toolchain

        val second = runner(tasks).build()
        assertContains(second.output, "Reusing configuration cache")
    }
}
