plugins { java }

check(extensions.findByName("kotlin") == null)
check(!plugins.hasPlugin("org.jetbrains.kotlin.jvm"))
// The settings plugin shares API interfaces, without bringing the KGP implementation into Java projects.
check(buildscript.classLoader.getResource("org/jetbrains/kotlin/gradle/plugin/KotlinPluginWrapper.class") == null)
