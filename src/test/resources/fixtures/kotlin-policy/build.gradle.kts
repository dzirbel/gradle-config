plugins { kotlin("jvm") version "2.4.10" apply false }
check(extensions.findByName("kotlin") == null)
