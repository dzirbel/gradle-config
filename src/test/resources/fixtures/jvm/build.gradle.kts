check(!plugins.hasPlugin("java-base")) { "The conventions must not turn an aggregation project into a JVM project." }
// Dependency Analysis must not bundle the Kotlin plugin implementation into Java-only builds.
check(buildscript.classLoader.getResource("org/jetbrains/kotlin/gradle/plugin/KotlinPluginWrapper.class") == null)
