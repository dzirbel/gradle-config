plugins { kotlin("jvm") }
dependencies { testImplementation(kotlin("test-junit")) }
check(kotlin.compilerOptions.optIn.get().isEmpty())
// JVM assertion behavior must come from the compiler setting, even without -ea.
tasks.test { enableAssertions = false }
