plugins {
    kotlin("jvm")
}

kotlin { jvmToolchain(21) }

gradleConfig { javaLanguageVersion = 25 }

dependencies { testImplementation(kotlin("test-junit")) }

tasks.register<JavaExec>("verifyRuntime") {
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "Example"
}
