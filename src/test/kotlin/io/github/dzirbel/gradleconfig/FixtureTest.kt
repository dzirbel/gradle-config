package io.github.dzirbel.gradleconfig

import org.gradle.testkit.runner.GradleRunner
import java.io.File
import java.nio.file.Files
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

abstract class FixtureTest(private val fixture: String) {
    protected lateinit var projectDir: File

    @BeforeTest
    fun copyFixture() {
        val directory = File(System.getProperty("testFixturesDirectory")).apply { mkdirs() }
        projectDir = Files.createTempDirectory(directory.toPath(), "$fixture-").toFile()
        File(requireNotNull(javaClass.getResource("/fixtures/$fixture")).toURI()).copyRecursively(projectDir)
    }

    @AfterTest
    fun deleteFixture() {
        if (::projectDir.isInitialized) projectDir.deleteRecursively()
    }

    protected fun runner(vararg tasks: String): GradleRunner = runner(tasks.toList())

    protected fun runner(tasks: Collection<String>): GradleRunner {
        return GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments(
                listOf(
                    "--init-script",
                    File(requireNotNull(javaClass.getResource("/published-plugin.init.gradle.kts")).toURI()).absolutePath,
                    "-PgradleConfigRepository=${System.getProperty("pluginRepository")}",
                    "-PgradleConfigVersion=${System.getProperty("pluginVersion")}",
                ) + listOf(
                    "--stacktrace",
                    "--warning-mode=fail",
                    "--max-workers=2",
                ).filter { default -> tasks.none { it.substringBefore('=') == default.substringBefore('=') } } + tasks,
            )
    }
}
