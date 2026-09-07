package io.github.dzirbel.gradleconfig

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/** Writes a reviewable template under build/, without changing the consuming build's source files. */
@CacheableTask
abstract class GenerateGradleProperties : DefaultTask() {
    @get:Input
    abstract val contents: Property<String>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun generate() {
        val file = outputFile.get().asFile
        file.parentFile.mkdirs()
        file.writeText(contents.get(), Charsets.UTF_8)
    }
}
