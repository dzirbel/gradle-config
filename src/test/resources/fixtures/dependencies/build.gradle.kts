val aligned = configurations.create("aligned")
val conflict = configurations.create("conflict")
val transitiveConflict = configurations.create("transitiveConflict")
val dynamic = configurations.create("dynamic")
val changing = configurations.create("changing")
val transitiveDynamic = configurations.create("transitiveDynamic")
val transitiveChanging = configurations.create("transitiveChanging")

dependencies {
    add(aligned.name, "test:library:1.0")
    add(aligned.name, "test:leaf:2.0")
    add(conflict.name, "test:library:1.0")
    add(conflict.name, "test:leaf:1.0")
    add(transitiveConflict.name, "test:library:1.0")
    add(transitiveConflict.name, "test:older-library:1.0")
    add(dynamic.name, "test:leaf:2.+")
    add(changing.name, "test:leaf:2.0") { (this as ExternalModuleDependency).isChanging = true }
    add(transitiveDynamic.name, "test:dynamic-library:1.0")
    add(transitiveChanging.name, "test:changing-library:1.0")
}

for (configuration in listOf(aligned, conflict, transitiveConflict, dynamic, changing, transitiveDynamic, transitiveChanging)) {
    tasks.register("resolve" + configuration.name.replaceFirstChar(Char::uppercaseChar)) {
        // Resolving task inputs makes resolution failures fatal and supports configuration-cache reuse.
        inputs.files(configuration)
        doLast { logger.lifecycle("Resolved dependency graph") }
    }
}
