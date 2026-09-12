gradleConfig { javaLanguageVersion = 21 }

configurations.create("implementation")

dependencies {
    "implementation"("org.example:zebra:1.0")
    "implementation"("org.example:alpha:1.0")
}
