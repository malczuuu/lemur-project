pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention").version("1.0.0")
}

rootProject.name = "lemur-project"

include(":lemur-app")
include(":lemur-flyway")
include(":lemur-libs:lemur-bom")
include(":lemur-libs:lemur-migration")
include(":lemur-libs:lemur-testkit")
