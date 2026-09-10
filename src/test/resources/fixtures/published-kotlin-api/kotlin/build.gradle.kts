import org.jetbrains.kotlin.gradle.plugin.KotlinBasePlugin

plugins { kotlin("jvm") version "2.4.10" }

// The project still selects its own KGP version, newer than gradle-config's public API dependency.
check(plugins.withType<KotlinBasePlugin>().single().pluginVersion == "2.4.10")
check(kotlin.compilerOptions.allWarningsAsErrors.get())
check(kotlin.compilerOptions.optIn.get().isEmpty())
