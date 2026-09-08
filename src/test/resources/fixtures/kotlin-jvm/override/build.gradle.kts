plugins {
    kotlin("jvm")
}

gradleConfig { javaLanguageVersion = 21 }

dependencies { testImplementation(kotlin("test-junit")) }

tasks.register<JavaExec>("verifyRuntime") {
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "Example"
}
