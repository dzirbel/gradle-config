package io.github.dzirbel.gradleconfig

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class KotlinJvmProjectTest : FixtureTest("kotlin-jvm") {
    @Test
    fun `Kotlin JVM shares the Java toolchain and preserves explicit targets`() {
        val versions = mapOf(
            "default" to 69, // Java 25 convention
            "override" to 65, // gradleConfig override
            "java-explicit" to 65, // Java toolchain override
            "explicit" to 65, // Kotlin toolchain wins over gradleConfig
            "target" to 65, // Explicit bytecode targets with a Java 25 toolchain
        )
        val tasks = versions.keys.flatMap { listOf(":$it:verifyRuntime", ":$it:test") }
        val first = runner(tasks).build()
        assertContains(first.output, "Configuration cache entry stored")
        versions.forEach { (module, version) ->
            val javaClass = projectDir.resolve("$module/build/classes/java/main/Example.class")
            val kotlinClass = projectDir.resolve("$module/build/classes/kotlin/main/KotlinExample.class")
            assertEquals(version, javaClass.classVersion(), "$module Java bytecode")
            assertEquals(version, kotlinClass.classVersion(), "$module Kotlin bytecode")
            val testReport = projectDir.resolve("$module/build/test-results/test/TEST-RuntimeTest.xml").readText()
            assertContains(testReport, "tests=\"1\"", message = "$module must execute its runtime test")
            assertContains(testReport, "failures=\"0\"", message = "$module test JVM")
        }

        val second = runner(tasks).build()
        assertContains(second.output, "Reusing configuration cache")
    }
}
