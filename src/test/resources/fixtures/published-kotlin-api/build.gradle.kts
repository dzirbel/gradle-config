plugins { java }

check(extensions.findByName("kotlin") == null)
check(!plugins.hasPlugin("org.jetbrains.kotlin.jvm"))
