import org.gradle.api.GradleException
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("internal.common-convention")
    id("internal.junit-convention")
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

tasks.named("check") {
    dependsOn("checkPackageInfo")
}

// Usage:
//   ./gradlew checkPackageInfo
tasks.register<DefaultTask>("checkPackageInfo") {
    description = "Ensures every non-empty Java package contains a package-info.java file."
    group = "verification"

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
