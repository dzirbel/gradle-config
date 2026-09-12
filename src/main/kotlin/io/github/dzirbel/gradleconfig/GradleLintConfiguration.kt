package io.github.dzirbel.gradleconfig

import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal fun Project.configureGradleLint() {
    // Give script-only projects the same verification lifecycle as language projects.
    pluginManager.apply("base")
    pluginManager.apply("com.squareup.sort-dependencies")
    pluginManager.apply("com.diffplug.spotless")

    extensions.configure<SpotlessExtension> {
        kotlinGradle {
            // Each project owns its scripts; avoid child projects, test resources, and generated files.
            target("*.gradle.kts", "gradle/**/*.gradle.kts", "src/*/kotlin/**/*.gradle.kts")
            ktlint()
            trimTrailingWhitespace()
            endWithNewline()
        }
        groovyGradle {
            target("*.gradle", "gradle/**/*.gradle", "src/*/groovy/**/*.gradle")
            greclipse()
            trimTrailingWhitespace()
            endWithNewline()
        }
    }

    // Both tools can edit build scripts. Sort first when both fixes are requested together.
    tasks.matching { it.name.startsWith("spotless") }.configureEach {
        mustRunAfter(tasks.matching { it.name == "sortDependencies" })
    }
}
