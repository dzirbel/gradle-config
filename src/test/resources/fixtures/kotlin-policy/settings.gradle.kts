plugins { id("io.github.dzirbel.gradle-config") }
rootProject.name = "kotlin-policy-fixture"
include("valid", "warning", "extra-warning", "unused-result", "nullability", "override", "opt-in")
