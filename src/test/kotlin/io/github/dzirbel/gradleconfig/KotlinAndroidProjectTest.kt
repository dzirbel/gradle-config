package io.github.dzirbel.gradleconfig

import org.gradle.testkit.runner.TaskOutcome
import org.junit.Assume.assumeTrue
import java.io.File
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class KotlinAndroidProjectTest : FixtureTest("kotlin-android") {
    @Test
    fun `AGP built-in Kotlin receives strict policy and preserves the Android bytecode target`() {
        val sdk = System.getenv("ANDROID_HOME") ?: System.getenv("ANDROID_SDK_ROOT")
            ?: "${System.getProperty("user.home")}/Android/Sdk"
        assumeTrue("Android SDK 36 is required for this integration test", File(sdk, "platforms/android-36").isDirectory)
        val environment = System.getenv() + ("ANDROID_HOME" to sdk)
        val first = runner("compileDebugKotlin").withEnvironment(environment).build()
        assertEquals(TaskOutcome.SUCCESS, first.task(":compileDebugKotlin")?.outcome)
        val greeter = projectDir.resolve("build").walkTopDown().single { it.name == "Greeter.class" }
        assertEquals(55, greeter.classVersion())
        assertContains(first.output, "Configuration cache entry stored")
        assertContains(runner("compileDebugKotlin").withEnvironment(environment).build().output, "Reusing configuration cache")
    }
}
