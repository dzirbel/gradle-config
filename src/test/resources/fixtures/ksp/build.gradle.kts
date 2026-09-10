plugins {
    kotlin("jvm") version "2.4.10" apply false
    id("com.google.devtools.ksp") version "2.3.12" apply false
}
check(extensions.findByName("ksp") == null)
