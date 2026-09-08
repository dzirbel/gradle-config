import org.gradle.buildconfiguration.tasks.UpdateDaemonJvm

// Exercise an aggregation root without a Java plugin.
gradleConfig {
    providers.gradleProperty("javaVersion").map(String::toInt).orNull?.let(javaLanguageVersion::set)
}

tasks.named<UpdateDaemonJvm>("updateDaemonJvm") {
    // Test criteria generation with installed JDKs, without external download resolution.
    toolchainDownloadUrls.empty()
    providers.gradleProperty("daemonVersion").map { JavaLanguageVersion.of(it.toInt()) }.orNull?.let(languageVersion::set)
}
