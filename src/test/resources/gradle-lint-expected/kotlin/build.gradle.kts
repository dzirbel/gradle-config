val implementation = configurations.create("implementation")
val testImplementation = configurations.create("testImplementation")

dependencies {
    constraints {
        implementation("org.example:alpha:1.0")
        implementation("org.example:zebra:1.0")
    }

    implementation(project(":container:leaf"))
    // Keep this comment and exclusion attached to alpha.
    implementation(libs.alpha) {
        exclude(group = "org.example", module = "excluded")
    }
    implementation(libs.zebra)

    testImplementation("org.example:test:1.0")
}
