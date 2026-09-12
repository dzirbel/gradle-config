package io.github.dzirbel.gradleconfig

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.testing.AbstractTestTask
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestOutputEvent
import org.gradle.api.tasks.testing.TestOutputListener
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.withType

internal fun Project.configureTests() {
    tasks.withType<AbstractTestTask>().configureEach {
        testLogging {
            // Keep normal runs quiet; --info and --debug retain Gradle's verbose logging.
            events = setOf(TestLogEvent.FAILED)
            exceptionFormat = TestExceptionFormat.FULL
            setStackTraceFilters(emptyList<Any>())
        }

        val outputListener = QuietTestOutputListener()
        addTestOutputListener(outputListener)
        // Let tests and reports finish first. An existing test failure keeps its original diagnostics.
        doLast { outputListener.verify() }
    }
}

private class QuietTestOutputListener : TestOutputListener {
    private var firstOutput: String? = null

    @Synchronized
    override fun onOutput(testDescriptor: TestDescriptor, outputEvent: TestOutputEvent) {
        if (firstOutput == null && outputEvent.message.isNotEmpty()) {
            firstOutput = "$testDescriptor wrote to ${outputEvent.destination}:\n${outputEvent.message}"
        }
    }

    @Synchronized
    fun verify() {
        firstOutput?.let { throw GradleException("Tests must not write to stdout or stderr.\n$it") }
    }
}
