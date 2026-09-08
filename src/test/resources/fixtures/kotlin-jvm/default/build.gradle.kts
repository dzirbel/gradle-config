plugins {
    kotlin("jvm")
}

dependencies { testImplementation(kotlin("test-junit")) }

tasks.register<JavaExec>("verifyRuntime") {
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "Example"
}
