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
                create<Application>("launch lemur-app") {
                    mainClass = "io.github.malczuuu.lemur.app.LemurApplicationKt"
                    moduleName = "lemur-project.lemur-app.main"
                    workingDirectory = rootProject.rootDir.absolutePath
                    programParameters = ""
                }
                create<Application>("launch lemur-flyway") {
                    mainClass = "io.github.malczuuu.lemur.flyway.FlywayApplicationKt"
                    moduleName = "lemur-project.lemur-flyway.main"
                    workingDirectory = rootProject.rootDir.absolutePath
                    programParameters = ""
                }
                create<Gradle>("build project") {
                    taskNames = listOf("spotlessApply build")
                    projectPath = rootProject.rootDir.absolutePath
                }
                create<Gradle>("format code") {
                    taskNames = listOf("spotlessApply")
                    projectPath = rootProject.rootDir.absolutePath
                }
                create<JUnit>("test lemur-app") {
                    moduleName = "lemur-project.lemur-app.test"
                    workingDirectory = rootProject.rootDir.absolutePath
                    packageName = "io.github.malczuuu.lemur.app"
                }
                create<JUnit>("test lemur-flyway") {
                    moduleName = "lemur-project.lemur-flyway.test"
                    workingDirectory = rootProject.rootDir.absolutePath
                    packageName = "io.github.malczuuu.lemur.flyway"
                }
                create<JUnit>("test lemur-testkit") {
                    moduleName = "lemur-project.lemur-libs.lemur-testkit.test"
                    workingDirectory = rootProject.rootDir.absolutePath
                    packageName = "io.github.malczuuu.lemur.testkit"
                }
            }
        }
    }
}
