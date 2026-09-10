plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
}
dependencies { ksp(project(":processor")) }
check(ksp.allWarningsAsErrors)
