plugins {
    kotlin("jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
kotlin { compilerOptions.jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21 }

dependencies { testImplementation(kotlin("test-junit")) }

tasks.register<JavaExec>("verifyRuntime") {
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "Example"
}
