plugins {
    kotlin("jvm")
}

java { toolchain.languageVersion = JavaLanguageVersion.of(21) }

dependencies { testImplementation(kotlin("test-junit")) }

tasks.register<JavaExec>("verifyRuntime") {
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "Example"
}
