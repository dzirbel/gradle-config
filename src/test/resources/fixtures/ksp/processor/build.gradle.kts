plugins { kotlin("jvm") }
// Processors run in the Gradle runtime, which is Java 21 in these tests.
gradleConfig { javaLanguageVersion = 21 }
dependencies { implementation("com.google.devtools.ksp:symbol-processing-api:2.3.12") }
check(extensions.findByName("ksp") == null)
