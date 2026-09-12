val aligned = configurations.create("aligned")
val conflict = configurations.create("conflict")

dependencies {
    add(aligned.name, "test:library:1.0")
    add(aligned.name, "test:leaf:2.0")
    add(conflict.name, "test:library:1.0")
    add(conflict.name, "test:leaf:1.0")
}

for (configuration in listOf(aligned, conflict)) {
    tasks.register("resolve" + configuration.name.replaceFirstChar(Char::uppercaseChar)) {
        inputs.files(configuration)
        doLast { logger.lifecycle("Resolved dependency graph") }
    }
}
