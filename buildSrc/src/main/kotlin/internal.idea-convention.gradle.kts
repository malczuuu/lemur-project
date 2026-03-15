import org.jetbrains.gradle.ext.Application
import org.jetbrains.gradle.ext.Gradle
import org.jetbrains.gradle.ext.JUnit
import org.jetbrains.gradle.ext.runConfigurations
import org.jetbrains.gradle.ext.settings

plugins {
    id("org.jetbrains.gradle.plugin.idea-ext")
}

idea {
    project {
        settings {
            runConfigurations {
                create<Application>("Run [lemur-app]") {
                    mainClass = "io.github.malczuuu.lemur.app.LemurApplicationKt"
                    moduleName = "lemur-project.lemur-app.main"
                    workingDirectory = rootProject.rootDir.absolutePath
                    programParameters = ""
                }
                create<Application>("Run [lemur-flyway]") {
                    mainClass = "io.github.malczuuu.lemur.flyway.FlywayApplicationKt"
                    moduleName = "lemur-project.lemur-flyway.main"
                    workingDirectory = rootProject.rootDir.absolutePath
                    programParameters = ""
                }
                create<Gradle>("Build [lemur-project]") {
                    taskNames = listOf("spotlessApply build")
                    projectPath = rootProject.rootDir.absolutePath
                }
                create<Gradle>("Test [lemur-project]") {
                    taskNames = listOf("test")
                    projectPath = rootProject.rootDir.absolutePath
                }
                create<Gradle>("Test [lemur-project|containers]") {
                    taskNames = listOf("test -Pcontainers.enabled")
                    projectPath = rootProject.rootDir.absolutePath
                }
                create<Gradle>("Format Code [lemur-project]") {
                    taskNames = listOf("spotlessApply")
                    projectPath = rootProject.rootDir.absolutePath
                }
                create<JUnit>("JUnit [lemur-app]") {
                    moduleName = "lemur-project.lemur-app.test"
                    workingDirectory = rootProject.rootDir.absolutePath
                    packageName = "io.github.malczuuu.lemur.app"
                }
                create<JUnit>("JUnit [lemur-flyway]") {
                    moduleName = "lemur-project.lemur-flyway.test"
                    workingDirectory = rootProject.rootDir.absolutePath
                    packageName = "io.github.malczuuu.lemur.flyway"
                }
                create<JUnit>("JUnit [lemur-contract]") {
                    moduleName = "lemur-project.lemur-libs.lemur-contract.test"
                    workingDirectory = rootProject.rootDir.absolutePath
                    packageName = "io.github.malczuuu.lemur.contract"
                }
                create<JUnit>("JUnit [lemur-testkit]") {
                    moduleName = "lemur-project.lemur-libs.lemur-testkit.test"
                    workingDirectory = rootProject.rootDir.absolutePath
                    packageName = "io.github.malczuuu.lemur.testkit"
                }
            }
        }
    }
}
