plugins { java }

dependencies { testImplementation("junit:junit:4.13.2") }

tasks.test {
    useJUnit()
    include("**/QuietTest.class")
}

tasks.register<Test>("failingTest") {
    testClassesDirs = sourceSets.test.get().output.classesDirs
    classpath = sourceSets.test.get().runtimeClasspath
    useJUnit()
    include("**/FailingTest.class")
}

tasks.register<Test>("outputTest") {
    testClassesDirs = sourceSets.test.get().output.classesDirs
    classpath = sourceSets.test.get().runtimeClasspath
    useJUnit()
    include("**/*OutputTest.class")
}
