import kotlin.test.Test

class RuntimeTest {
    @Test
    fun usesConfiguredToolchain() {
        // Exercise the same runtime assertion inside Gradle's forked test JVM.
        Example.main(emptyArray())
    }
}
