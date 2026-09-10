import org.jetbrains.kotlin.gradle.dsl.JvmDefaultMode

plugins { kotlin("jvm") }
kotlin {
    compilerOptions {
        allWarningsAsErrors = false
        progressiveMode = false
        freeCompilerArgs.set(emptyList())
        optIn.set(emptyList())
        jvmDefault = JvmDefaultMode.ENABLE
        javaParameters = false
    }
}
