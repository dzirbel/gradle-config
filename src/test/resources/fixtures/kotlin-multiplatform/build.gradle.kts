import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins { kotlin("multiplatform") version "2.4.10" }

kotlin {
    jvm()
    js { nodejs() }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs { nodejs() }
    linuxX64()
    sourceSets.commonTest.dependencies { implementation(kotlin("test")) }
}
