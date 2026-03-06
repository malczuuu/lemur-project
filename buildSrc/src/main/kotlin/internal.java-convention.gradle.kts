import org.gradle.api.GradleException
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("internal.common-convention")
    id("java")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}

tasks.withType<Jar>().configureEach {
    manifest {
        attributes["Implementation-Title"] = project.name
        attributes["Implementation-Version"] = project.version
        attributes["Created-By"] = "Gradle ${gradle.gradleVersion}"
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform {
        if (project.findProperty("containers.enabled")?.toString() == "false") {
            excludeTags("testcontainers")
        }
    }

    testLogging {
        events(TestLogEvent.FAILED, TestLogEvent.PASSED, TestLogEvent.SKIPPED)
        exceptionFormat = TestExceptionFormat.SHORT
        showStandardStreams = true
    }

    // For resolving warnings from mockito.
    jvmArgs("-XX:+EnableDynamicAgentLoading")

    systemProperty("user.language", "en")
    systemProperty("user.country", "US")
}

tasks.named("check") {
    dependsOn("checkPackageInfo")
}

tasks.register<DefaultTask>("checkPackageInfo") {
    group = "verification"
    description = "Ensures every non-empty Java package contains package-info.java"

    val root = file("src/main/java")

    doLast {
        val violations = mutableListOf<String>()

        root.walkTopDown()
            .filter { it.isDirectory }
            .forEach { dir ->
                val javaFiles = dir.listFiles()?.filter { it.name.endsWith(".java") } ?: emptyList()
                val hasPackageInfo = javaFiles.any { it.name == "package-info.java" }
                val nonPackageInfoFiles = javaFiles.filter { it.name != "package-info.java" }

                if (nonPackageInfoFiles.isNotEmpty() && !hasPackageInfo) {
                    violations.add(dir.path)
                }
            }

        if (violations.isNotEmpty()) {
            throw GradleException("Missing package-info.java in:\n" + violations.joinToString("\n") { " - $it" })
        }
    }
}
