val implementation = configurations.create("implementation")
val testImplementation = configurations.create("testImplementation")

dependencies {
    testImplementation("org.example:test:1.0")
    implementation(libs.zebra)
    // Keep this comment and exclusion attached to alpha.
    implementation(libs.alpha) {
        exclude(group = "org.example", module = "excluded")
    }
    implementation(project(":container:leaf"))
    constraints {
        implementation("org.example:zebra:1.0")
        implementation("org.example:alpha:1.0")
    }
}
