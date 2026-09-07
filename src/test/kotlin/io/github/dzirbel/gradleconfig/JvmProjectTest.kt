package io.github.dzirbel.gradleconfig

import java.io.DataInputStream
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class JvmProjectTest : FixtureTest("jvm") {
    @Test
    fun `settings plugin configures all JVM projects and respects overrides under isolation`() {
        val tasks = arrayOf(":default:compileJava", ":override:compileJava", ":explicit:compileJava")
        val first = runner(*tasks).build()
        assertContains(first.output, "Configuration cache entry stored")

        assertEquals(69, classVersion("default")) // Java 25
        assertEquals(65, classVersion("override")) // Java 21 via gradleConfig
        assertEquals(65, classVersion("explicit")) // Java 21 via java.toolchain

        val second = runner(*tasks).build()
        assertContains(second.output, "Reusing configuration cache")
    }

    private fun classVersion(module: String): Int = DataInputStream(
        projectDir.resolve("$module/build/classes/java/main/Example.class").inputStream(),
    ).use {
        it.readInt()
        it.readUnsignedShort()
        it.readUnsignedShort()
    }
}
