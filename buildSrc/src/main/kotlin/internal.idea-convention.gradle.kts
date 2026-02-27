import org.jetbrains.gradle.ext.Application
import org.jetbrains.gradle.ext.Gradle
import org.jetbrains.gradle.ext.runConfigurations
import org.jetbrains.gradle.ext.settings

plugins {
    id("org.jetbrains.gradle.plugin.idea-ext")
}

idea {
    project {
        settings {
            runConfigurations {
                create<Application>("launch lemur-app") {
                    mainClass = "io.github.malczuuu.lemur.app.LemurApplication"
                    moduleName = "lemur-project.lemur-app.main"
                    workingDirectory = rootProject.rootDir.absolutePath
                    programParameters = ""
                }
                create<Application>("launch lemur-flyway") {
                    mainClass = "io.github.malczuuu.lemur.flyway.FlywayApplication"
                    moduleName = "lemur-project.lemur-flyway.main"
                    workingDirectory = rootProject.rootDir.absolutePath
                    programParameters = ""
                }
                create<Gradle>("build project") {
                    taskNames = listOf("spotlessApply build")
                    projectPath = rootProject.rootDir.absolutePath
                }
                create<Gradle>("test project") {
                    taskNames = listOf("test --rerun-tasks")
                    projectPath = rootProject.rootDir.absolutePath
                }
                create<Gradle>("test project [with containers]") {
                    taskNames = listOf("test --rerun-tasks -Pcontainers.enabled")
                    projectPath = rootProject.rootDir.absolutePath
                }
            }
        }
    }
}
