plugins {
    id("com.android.library") version "9.4.0"
    // Select Kotlin for AGP's built-in integration without applying kotlin-android.
    kotlin("android") version "2.4.10" apply false
}

android {
    namespace = "io.github.dzirbel.fixture"
    compileSdk = 36
    defaultConfig { minSdk = 26 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

check(!plugins.hasPlugin("org.jetbrains.kotlin.android"))
check(kotlin.compilerOptions.allWarningsAsErrors.get())
check(!kotlin.compilerOptions.extraWarnings.get())
check(kotlin.compilerOptions.optIn.get().isEmpty())
